package org.orienteer.logger.server.service.dispatcher;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.logger.OLoggerEvent;
import org.orienteer.logger.server.model.OLoggerEventMailDispatcherModel;
import org.orienteer.logger.server.model.OLoggerEventModel;
import org.orienteer.logger.server.repository.OLoggerRepository;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.service.IOMailService;
import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;

import java.util.LinkedList;
import java.util.Map;

/**
 * Event dispatcher which sends events to mail
 */
public class OLoggerEventMailDispatcher extends OLoggerEventFilteredDispatcher {

    public OLoggerEventMailDispatcher(String alias) {
        super(alias);
    }

    @Override
    protected void onDispatchEvent(OLoggerEventModel eventModel, OLoggerEvent event) {
        OLoggerEventMailDispatcherModel dispatcher = OLoggerRepository.getOLoggerEventMailDispatcher(getAlias())
                .orElseThrow(() -> new IllegalStateException("There is no mail dispatcher with alias: " + getAlias()));

        Map<String, Object> macros = new ODocumentMapWrapper(eventModel.getDocument());
        OPreparedMail mail = new OPreparedMail(dispatcher.getMail(), macros);
        mail.setRecipients(new LinkedList<>(dispatcher.getRecipients()));
        getMailService().sendMailAsync(mail);
    }


    private IOMailService getMailService() {
        return OrienteerWebApplication.lookupApplication().getServiceInstance(IOMailService.class);
    }
}
