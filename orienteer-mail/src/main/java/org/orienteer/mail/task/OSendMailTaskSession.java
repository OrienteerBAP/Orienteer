package org.orienteer.mail.task;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.core.tasks.OTaskSession;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.orienteer.core.util.CommonUtils.mapIdentifiables;

/**
 * Task session which prepared during application lifecycle and then used in {@link IOSendMailTask} for send mails
 */
public class OSendMailTaskSession extends OTaskSession {

    /**
     * OrientDB class name
     */
    public static final String CLASS_NAME = "OSendMailTaskSession";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#LINKLIST}
     * Link list of {@link OPreparedMail} which need send
     */
    public static final String PROP_MAILS = "mails";

    public OSendMailTaskSession() {
        this(new ODocument(CLASS_NAME));
    }

    public OSendMailTaskSession(ODocument sessionDoc) {
        super(sessionDoc);
    }

    /**
     * Add new {@link OPreparedMail} to this session
     * @param mail {@link OPreparedMail} mail for add
     * @return link to this session instance
     */
    public OSendMailTaskSession addMail(OPreparedMail mail) {
        List<OPreparedMail> mails = new LinkedList<>(getMails());
        mails.add(mail);
        return setMails(mails);
    }

    /**
     * Remove {@link OPreparedMail} from this session
     * @param mail {@link OPreparedMail} mail for remove
     * @return link to this session instance
     */
    public OSendMailTaskSession removeMail(OPreparedMail mail) {
        List<OPreparedMail> mails = new LinkedList<>(getMails());
        if (mails.remove(mail)) {
            setMails(mails);
        }
        return this;
    }

    public OSendMailTaskSession setMails(List<OPreparedMail> mails) {
        return setMailsAsDocuments(mails.stream().map(OPreparedMail::getDocument).collect(Collectors.toList()));
    }

    public OSendMailTaskSession setMailsAsDocuments(List<ODocument> mails) {
        document.field(PROP_MAILS, mails);
        return this;
    }

    public List<OPreparedMail> getMails() {
        return mapIdentifiables(document.field(PROP_MAILS), OPreparedMail::new);
    }
}
