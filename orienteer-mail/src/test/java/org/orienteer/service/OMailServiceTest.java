package org.orienteer.service;

import org.orienteer.model.OPreparedMail;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

public class OMailServiceTest extends OMailServiceImpl {

    private Consumer<OPreparedMail> mailChecker;

    @Override
    public void sendMail(OPreparedMail mail) throws MessagingException, UnsupportedEncodingException {
        if (mailChecker != null) {
            mailChecker.accept(mail);
        } else throw new IllegalStateException("Mail checker can't be null!");
    }

    public OMailServiceTest onSendMail(Consumer<OPreparedMail> mailChecker) {
        this.mailChecker = mailChecker;
        return this;
    }
}
