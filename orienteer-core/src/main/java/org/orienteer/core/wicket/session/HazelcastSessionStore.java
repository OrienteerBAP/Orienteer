package org.orienteer.core.wicket.session;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.request.Request;
import org.apache.wicket.session.ISessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

/**
 * Implementation of {@link ISessionStore} for store sessions inside Hazelcast map
 */
public class HazelcastSessionStore implements ISessionStore {

    private static final Logger LOG = LoggerFactory.getLogger(HazelcastSessionStore.class);

    public static final String COOKIE_JSESSIONID = "JSESSIONID";

    private final Set<UnboundListener> unboundListeners = new CopyOnWriteArraySet<>();
    private final Set<BindListener> bindListeners       = new CopyOnWriteArraySet<>();

    /**
     * Hazelcast distributed map
     */
    private final IMap<String, HazelcastSession> sessionStore;

    /**
     * Local store for save sessions in current member
     */
    private final Map<String, HazelcastSession> localStore;


    public HazelcastSessionStore() {
        HazelcastInstance hazelcast = Hazelcast.getHazelcastInstanceByName("orienteer-hazelcast");
        sessionStore = hazelcast.getMap("wicket-sessions");
        localStore = new HashMap<>();
    }

    @Override
    public Serializable getAttribute(Request request, String name) {
        Serializable serializable = getHazelcastSession(request)
                .map(session -> session.getAttribute(name))
                .orElse(null);
        LOG.debug("get attribute {} - {}", name, serializable);
        return serializable;
    }

    @Override
    public List<String> getAttributeNames(Request request) {
        return getHazelcastSession(request)
                .map(HazelcastSession::getAttributes)
                .map(Map::keySet)
                .<List<String>>map(LinkedList::new)
                .orElse(Collections.emptyList());
    }

    @Override
    public void setAttribute(Request request, String name, Serializable value) {
        getHazelcastSession(request)
                .ifPresent(session -> {
                    session.setAttribute(name, value);
                    sessionStore.put(session.getId(), session);
                });
    }

    @Override
    public void removeAttribute(Request request, String name) {
        getHazelcastSession(request)
                .ifPresent(session -> {
                    session.removeAttribute(name);
                    sessionStore.put(session.getId(), session);
                });
    }

    @Override
    public void invalidate(Request request) {
        HttpSession httpSession = getHttpSession(request, false);
        if (httpSession != null) {
            // tell the app server the session is no longer valid
            httpSession.invalidate();
        }
        getSessionId(request).ifPresent(id -> {
            localStore.remove(id);
            sessionStore.remove(id);
        });
    }

    @Override
    public String getSessionId(Request request, boolean create) {
        if (create) {
            HazelcastSession session = getOrCreateHazelcastSession(request);
            return session != null ? session.getId() : null;
        }
        return getHazelcastSession(request)
                .map(HazelcastSession::getId)
                .orElse(null);
    }

    @Override
    public Session lookup(Request request) {
        String sessionId = getSessionId(request, false);
        if (sessionId != null) {
            return getWicketSession(request);
        }
        return null;
    }

    @Override
    public void bind(Request request, Session newSession) {
        Session wicketSession = getWicketSession(request);
        String existsId = wicketSession != null ? wicketSession.getId() : null;

        if (existsId == null || !newSession.getId().equals(existsId)) {
            // call template method
            onBind(request, newSession);
            for (BindListener listener : getBindListeners())
            {
                listener.bindingSession(request, newSession);
            }

            String applicationKey = Application.get().getName();
            String attributeName = "Wicket:SessionUnbindingListener-" + applicationKey;
            Serializable attributeValue = new HazelcastSessionStore.SessionBindingListener(applicationKey, newSession);

            getHazelcastSession(request).ifPresent(session -> {
                session.setAttribute(attributeName, attributeValue); // register an unbinding listener for cleaning up
                sessionStore.put(session.getId(), session);
                setWicketSession(request, newSession); // register the session object itself
            });
        }
    }

    @Override
    public void flushSession(Request request, Session session) {
        Session wicketSession = getWicketSession(request);
        String existsId = wicketSession != null ? wicketSession.getId() : null;
        if (existsId == null || !session.getId().equals(existsId)) {
            // this session is not yet bound, bind it
            bind(request, session);
        } else {
            setWicketSession(request, session);
        }
    }

    @Override
    public void destroy() {
        localStore.clear();
    }

    @Override
    public final void registerUnboundListener(final UnboundListener listener) {
        unboundListeners.add(listener);
    }

    @Override
    public final void unregisterUnboundListener(final UnboundListener listener) {
        unboundListeners.remove(listener);
    }

    @Override
    public final Set<UnboundListener> getUnboundListener() {
        return Collections.unmodifiableSet(unboundListeners);
    }

    /**
     * Registers listener invoked when session is bound.
     *
     * @param listener
     */
    @Override
    public void registerBindListener(BindListener listener) {
        bindListeners.add(listener);
    }

    /**
     * Unregisters listener invoked when session is bound.
     *
     * @param listener
     */
    @Override
    public void unregisterBindListener(BindListener listener) {
        bindListeners.remove(listener);
    }

    /**
     * @return The list of registered bind listeners
     */
    @Override
    public Set<BindListener> getBindListeners() {
        return Collections.unmodifiableSet(bindListeners);
    }

    private HazelcastSession getOrCreateHazelcastSession(Request request) {
        return getHazelcastSession(request)
                .orElseGet(() -> {
                    String id = getSessionId(request).orElse(null);
                    if (id != null) {
                        HazelcastSession session = new HazelcastSession(id, new HashMap<>());
                        sessionStore.put(session.getId(), session);
                        localStore.put(session.getId(), session);

                        IRequestLogger logger = Application.get().getRequestLogger();
                        if (logger != null) {
                            logger.sessionCreated(session.getId());
                        }
                        return session;
                    }
                    return null;
                });
    }

    private Optional<HazelcastSession> getHazelcastSession(Request request) {
        return getSessionId(request)
                .map(id -> {
                    HazelcastSession session = localStore.get(id);
                    if (session == null) {
                        session = sessionStore.get(id);
                        if (session != null) {
                            localStore.put(id, session);
                        }
                    }
                    return session;
                });
    }

    private Optional<String> getSessionId(Request request) {
        HttpServletRequest servletRequest = getHttpServletRequest(request);
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        return Stream.of(cookies)
                .filter(c -> c.getName().equals(COOKIE_JSESSIONID))
                .map(Cookie::getValue)
                .filter(Objects::nonNull)
                .map(value -> value.split("\\.")[0])
                .findFirst();
    }

    /**
     * @param request The Wicket request
     * @return The http servlet request
     */
    protected final HttpServletRequest getHttpServletRequest(final Request request) {
        Object containerRequest = request.getContainerRequest();
        if (containerRequest instanceof HttpServletRequest) {
            return (HttpServletRequest)containerRequest;
        }
        throw new IllegalArgumentException("Request must be ServletWebRequest");
    }

    /**
     *
     * @see HttpServletRequest#getSession(boolean)
     *
     * @param request
     *            A Wicket request object
     * @param create
     *            If true, a session will be created if it is not existing yet
     * @return The HttpSession associated with this request or null if {@code create} is false and
     *         the {@code request} has no valid session
     */
    protected HttpSession getHttpSession(final Request request, final boolean create) {
        return getHttpServletRequest(request).getSession(create);
    }

    /**
     * Template method that is called when a session is being bound to the session store. It is
     * called <strong>before</strong> the session object itself is added to this store (which is
     * done by calling {@link ISessionStore#setAttribute(Request, String, Serializable)} with key
     * {@link Session#SESSION_ATTRIBUTE_NAME}.
     *
     * @param request
     *            The request
     * @param newSession
     *            The new session
     */
    protected void onBind(final Request request, final Session newSession)
    {
    }

    /**
     * Template method that is called when the session is being detached from the store, which
     * typically happens when the {@link HttpSession} was invalidated.
     *
     * @param sessionId
     *            The session id of the session that was invalidated.
     */
    protected void onUnbind(final String sessionId)
    {
    }

    /**
     * Reads the Wicket {@link Session} from the {@link HttpSession}'s attribute
     *
     * @param request The Wicket request
     * @return The Wicket Session or {@code null}
     */
    protected Session getWicketSession(final Request request) {
        return (Session) getAttribute(request, Session.SESSION_ATTRIBUTE_NAME);
    }

    /**
     * Stores the Wicket {@link Session} in an attribute in the {@link HttpSession}
     *
     * @param request The Wicket request
     * @param session The Wicket session
     */
    protected void setWicketSession(final Request request, final Session session) {
        setAttribute(request, Session.SESSION_ATTRIBUTE_NAME, session);
    }

    /**
     * Reacts on unbinding from the session by cleaning up the session related data.
     */
    private static final class SessionBindingListener implements HttpSessionBindingListener, Serializable {
        private static final long serialVersionUID = 1L;

        /** The unique key of the application within this web application. */
        private final String applicationKey;

        /**
         * The Wicket Session associated with the expiring HttpSession
         */
        private final Session wicketSession;

        /**
         * Construct.
         *
         * @param applicationKey
         *            The unique key of the application within this web application
         * @param wicketSession
         *            The Wicket Session associated with the expiring http session
         */
        public SessionBindingListener(final String applicationKey, final Session wicketSession)
        {
            this.applicationKey = applicationKey;
            this.wicketSession = wicketSession;
        }

        @Override
        public void valueBound(final HttpSessionBindingEvent evg)
        {
        }

        @Override
        public void valueUnbound(final HttpSessionBindingEvent evt) {
            String sessionId = evt.getSession().getId();

            LOG.debug("Session unbound: {}", sessionId);

            if (wicketSession != null) {
                wicketSession.onInvalidate();
            }

            Application application = Application.get(applicationKey);
            if (application == null) {
                LOG.debug("Wicket application with name '{}' not found.", applicationKey);
                return;
            }

            ISessionStore sessionStore = application.getSessionStore();
            if (sessionStore != null) {
                if (sessionStore instanceof HazelcastSessionStore) {
                    ((HazelcastSessionStore) sessionStore).onUnbind(sessionId);
                }

                for (UnboundListener listener : sessionStore.getUnboundListener()) {
                    listener.sessionUnbound(sessionId);
                }
            }
        }
    }

    /**
     * Class which represents Wicket session in Hazelcast data structure
     */
    private static class HazelcastSession implements Serializable {

        public final String id;
        public final Map<String, Serializable> attributes;

        public HazelcastSession(String id, Map<String, Serializable> attributes) {
            this.id = id;
            this.attributes = attributes;
        }

        public String getId() {
            return id;
        }

        public Map<String, Serializable> getAttributes() {
            return attributes;
        }

        public HazelcastSession setAttribute(String name, Serializable attribute) {
            attributes.put(name, attribute);
            return this;
        }

        public Serializable getAttribute(String name) {
            return attributes.get(name);
        }

        public HazelcastSession removeAttribute(String name) {
            attributes.remove(name);
            return this;
        }
    }

}
