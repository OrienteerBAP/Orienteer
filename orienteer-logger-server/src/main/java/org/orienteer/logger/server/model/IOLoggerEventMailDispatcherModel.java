package org.orienteer.logger.server.model;

import java.util.Set;

import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.mail.model.OMail;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.OrientDBProperty;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Wrapper for mail dispatcher
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(IOLoggerEventMailDispatcherModel.CLASS_NAME)
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
    
    @EntityProperty(value = "mail", referencedType = OMail.CLASS_NAME)
    @OrientDBProperty(notNull = true)
    public ODocument getMailAsDocument();
    @EntityProperty(value = "mail", referencedType = OMail.CLASS_NAME)
    @OrientDBProperty(notNull = true)
    public IOLoggerEventFilteredDispatcherModel setMailAsDocument(ODocument mail);
    
    @OrientDBProperty(notNull = true)
    public Set<String> getRecipients();
	public IOLoggerEventMailDispatcherModel setRecipients(Set<String> value);

}
