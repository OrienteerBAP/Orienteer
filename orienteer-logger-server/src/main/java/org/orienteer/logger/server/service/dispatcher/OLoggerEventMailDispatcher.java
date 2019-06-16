package org.orienteer.logger.server.service.dispatcher;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.logger.OLoggerEvent;
import org.orienteer.logger.server.model.OLoggerEventMailDispatcherModel;
import org.orienteer.logger.server.model.OLoggerEventModel;
import org.orienteer.logger.server.repository.OLoggerModuleRepository;
import org.orienteer.logger.server.repository.OLoggerRepository;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.service.IOMailService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Event dispatcher which sends events to mail
 */
public class OLoggerEventMailDispatcher extends OLoggerEventFilteredDispatcher {

    public static final String LINK_MARCOS = "link";

    public OLoggerEventMailDispatcher(String alias) {
        super(alias);
    }

    @Override
    protected void onDispatchEvent(OLoggerEventModel eventModel, OLoggerEvent event) {
        OLoggerEventMailDispatcherModel dispatcher = OLoggerRepository.getOLoggerEventMailDispatcher(getAlias())
                .orElseThrow(() -> new IllegalStateException("There is no mail dispatcher with alias: " + getAlias()));

        Map<String, Object> macros = createMacros(eventModel);
        OPreparedMail mail = new OPreparedMail(dispatcher.getMail(), macros);
        mail.setRecipients(new LinkedList<>(dispatcher.getRecipients()));
        getMailService().sendMailAsync(mail);
    }

    private Map<String, Object> createMacros(OLoggerEventModel model) {
        Map<String, Object> macros = new HashMap<>();
        macros.put(OLoggerEventModel.PROP_EVENT_ID, wrapData(model.getEventId()));
        macros.put(OLoggerEventModel.PROP_APPLICATION, wrapData(model.getApplication()));
        macros.put(OLoggerEventModel.PROP_NODE_ID, wrapData(model.getNodeId()));
        macros.put(OLoggerEventModel.PROP_CORRELATION_ID, wrapData(model.getCorrelationId()));
        macros.put(OLoggerEventModel.PROP_DATE_TIME, wrapData(model.getDatetime()));
        macros.put(OLoggerEventModel.PROP_REMOTE_ADDRESS, wrapData(model.getRemoteAddress()));
        macros.put(OLoggerEventModel.PROP_HOST_NAME, wrapData(model.getHostName()));
        macros.put(OLoggerEventModel.PROP_USERNAME, wrapData(model.getUsername()));
        macros.put(OLoggerEventModel.PROP_CLIENT_URL, wrapData(model.getClientUrl()));
        macros.put(OLoggerEventModel.PROP_SUMMARY, wrapData(model.getSummary()));
        macros.put(OLoggerEventModel.PROP_MESSAGE, wrapData(model.getMessage()));
        macros.put(OLoggerEventModel.PROP_SEED_CLASS, wrapData(model.getSeedClass()));
        macros.put(LINK_MARCOS, createLinkToEvent(model));

        return macros;
    }

    private String createLinkToEvent(OLoggerEventModel model) {
        RequestCycle cycle = RequestCycle.get();
        if (cycle != null) {
            PageParameters params = new PageParameters();
            params.add("rid", model.getDocument().getIdentity().toString().substring(1));
            CharSequence url = cycle.urlFor(ODocumentPage.class, params);
            return OLoggerModuleRepository.getModule().getDomain() + "/" + url;
        }
        return "";
    }

    private String wrapData(Object data) {
        return data != null ? data.toString() : "";
    }

    private IOMailService getMailService() {
        return OrienteerWebApplication.lookupApplication().getServiceInstance(IOMailService.class);
    }
}
