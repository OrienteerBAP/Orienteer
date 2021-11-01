package org.orienteer.mail.task;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.tasks.IOTaskSessionPersisted;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.orienteer.core.util.CommonUtils.mapIdentifiables;

/**
 * Task session which prepared during application lifecycle and then used in {@link IOSendMailTask} for send mails
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(IOSendMailTaskSession.CLASS_NAME)
public interface IOSendMailTaskSession extends IOTaskSessionPersisted {

    /**
     * OrientDB class name
     */
    public static final String CLASS_NAME = "OSendMailTaskSession";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#LINKLIST}
     * Link list of {@link OPreparedMail} which need send
     */
    public static final String PROP_MAILS = "mails";

    @EntityProperty(referencedType = OPreparedMail.CLASS_NAME)
    public List<OPreparedMail> getMails();
    public IOSendMailTaskSession setMails(List<OPreparedMail> mails);

    @EntityProperty(value = "mails", referencedType = OPreparedMail.CLASS_NAME)
    public List<ODocument> getMailsAsDocuments();
    @EntityProperty(value = "mails", referencedType = OPreparedMail.CLASS_NAME)
    public IOSendMailTaskSession setMailsAsDocuments(List<ODocument> mails);
    
    /**
     * Add new {@link OPreparedMail} to this session
     * @param mail {@link OPreparedMail} mail for add
     * @return link to this session instance
     */
    public default IOSendMailTaskSession addMail(OPreparedMail mail) {
        List<OPreparedMail> mails = new LinkedList<>(getMails());
        mails.add(mail);
        return setMails(mails);
    }

    /**
     * Remove {@link OPreparedMail} from this session
     * @param mail {@link OPreparedMail} mail for remove
     * @return link to this session instance
     */
    public default IOSendMailTaskSession removeMail(OPreparedMail mail) {
        List<OPreparedMail> mails = new LinkedList<>(getMails());
        if (mails.remove(mail)) {
            setMails(mails);
        }
        return this;
    }


}
