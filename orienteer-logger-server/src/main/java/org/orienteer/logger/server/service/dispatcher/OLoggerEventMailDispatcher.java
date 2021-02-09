package org.orienteer.logger.server.service.dispatcher;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.logger.OLoggerEvent;
import org.orienteer.logger.server.OLoggerModule;
import org.orienteer.logger.server.model.IOLoggerDAO;
import org.orienteer.logger.server.model.IOLoggerEventMailDispatcherModel;
import org.orienteer.logger.server.model.IOLoggerEventModel;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.service.IOMailService;

import com.google.common.collect.Maps;

import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;

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
    protected void onDispatchEvent(IOLoggerEventModel eventModel, OLoggerEvent event) {
        IOLoggerEventMailDispatcherModel dispatcher = IOLoggerDAO.INSTANCE.getOLoggerEventMailDispatcher(getAlias());
        if(dispatcher == null) throw new IllegalStateException("There is no mail dispatcher with alias: " + getAlias());

        Map<String, Object> macros = createMacros(eventModel);
        OPreparedMail mail = new OPreparedMail(dispatcher.getMail(), macros);
        mail.setRecipients(new LinkedList<>(dispatcher.getRecipients()));
        getMailService().sendMailAsync(mail);
    }

    private Map<String, Object> createMacros(IOLoggerEventModel model) {
        Map<String, Object> macros = new HashMap<>();
        macros.putAll(Maps.transformValues(new ODocumentMapWrapper(model.getDocument()), d->wrapData(d)));
        macros.put(LINK_MARCOS, createLinkToEvent(model));

        return macros;
    }

    private String createLinkToEvent(IOLoggerEventModel model) {
        RequestCycle cycle = RequestCycle.get();
        if (cycle != null) {
            PageParameters params = new PageParameters();
            params.add("rid", model.getDocument().getIdentity().toString().substring(1));
            CharSequence url = cycle.urlFor(ODocumentPage.class, params);
            return OLoggerModule.ILoggerModuleConfiguration.get().getDomain() + "/" + url;
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
