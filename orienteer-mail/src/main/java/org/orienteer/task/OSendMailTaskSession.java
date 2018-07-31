package org.orienteer.task;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.tasks.OTaskSession;
import org.orienteer.model.OPreparedMail;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.orienteer.core.util.CommonUtils.mapIdentifiables;

public class OSendMailTaskSession extends OTaskSession {

    public static final String CLASS_NAME = "OSendMailTaskSession";

    public static final String PROP_MAILS = "mails";

    public OSendMailTaskSession() {
        this(new ODocument(CLASS_NAME));
    }

    public OSendMailTaskSession(ODocument sessionDoc) {
        super(sessionDoc);
    }

    public OSendMailTaskSession addMail(OPreparedMail mail) {
        List<OPreparedMail> mails = new LinkedList<>(getMails());
        mails.add(mail);
        return setMails(mails);
    }

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
