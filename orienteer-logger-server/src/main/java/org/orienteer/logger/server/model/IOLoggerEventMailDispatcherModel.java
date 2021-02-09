package org.orienteer.logger.server.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.mail.model.OMail;

import java.util.Collections;
import java.util.Set;

/**
 * Wrapper for mail dispatcher
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOLoggerEventMailDispatcherModel.CLASS_NAME)
public interface IOLoggerEventMailDispatcherModel extends IOLoggerEventFilteredDispatcherModel {

    public static final String CLASS_NAME = "OLoggerEventMailDispatcher";

    public static final String PROP_MAIL       = "mail";
    public static final String PROP_RECIPIENTS = "recipients";

    public default OMail getMail() {
        ODocument mail = getMailAsDocument();
        return mail != null ? new OMail(mail) : null;
    }

    public default IOLoggerEventFilteredDispatcherModel setMail(OMail mail) {
    	return setMailAsDocument(mail != null ? mail.getDocument() : null);
    }
    
    @DAOField(value = "mail", linkedClass = OMail.CLASS_NAME, notNull = true)
    public ODocument getMailAsDocument();
    @DAOField(value = "mail", linkedClass = OMail.CLASS_NAME, notNull = true)
    public IOLoggerEventFilteredDispatcherModel setMailAsDocument(ODocument mail);
    
    @DAOField(notNull = true)
    public Set<String> getRecipients();
	public IOLoggerEventMailDispatcherModel setRecipients(Set<String> value);

}
