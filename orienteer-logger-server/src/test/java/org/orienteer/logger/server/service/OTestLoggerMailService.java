package org.orienteer.logger.server.service;

import org.orienteer.mail.model.OMailSettings;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.service.IOMailService;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class OTestLoggerMailService implements IOMailService {

    private final LinkedList<OPreparedMail> mails;

    public OTestLoggerMailService() {
        this.mails = new LinkedList<>();
    }

    @Override
    public void sendMail(OPreparedMail mail) throws MessagingException, UnsupportedEncodingException {
        mails.add(mail);
    }

    @Override
    public void sendMails(List<OPreparedMail> mails) throws MessagingException, UnsupportedEncodingException {
        this.mails.addAll(mails);
    }

    @Override
    public void sendMailAsync(OPreparedMail mail) {
        mails.add(mail);
    }

    @Override
    public void sendMailsAsync(List<OPreparedMail> mails) {
        this.mails.addAll(mails);
    }

    @Override
    public void sendMailAsync(OPreparedMail mail, Consumer<Boolean> f) {
        mails.add(mail);
    }

    @Override
    public void fetchMails(OMailSettings settings, String folderName, Consumer<Message> consumer) throws MessagingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Void> fetchMailsAsync(OMailSettings settings, String folderName, Consumer<Message> consumer) {
        throw new UnsupportedOperationException();
    }

    public LinkedList<OPreparedMail> getMails() {
        return mails;
    }
}
