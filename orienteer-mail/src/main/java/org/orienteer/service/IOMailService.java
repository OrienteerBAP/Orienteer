package org.orienteer.service;

import com.google.inject.ImplementedBy;
import org.orienteer.model.OMailSettings;
import org.orienteer.model.OPreparedMail;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Service for simple work with email
 */
@ImplementedBy(OMailServiceImpl.class)
public interface IOMailService {

    /**
     * Send mail to recipients
     * @param mail {@link OPreparedMail} prepared mail which will be send
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public void sendMail(OPreparedMail mail) throws MessagingException, UnsupportedEncodingException;

    /**
     * Send mails to recipients
     * @param mails {@link List<OPreparedMail>} prepared mails which will be send
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public void sendMails(List<OPreparedMail> mails) throws MessagingException, UnsupportedEncodingException;

    /**
     * Send mail to recipients asynchronous
     * @param mail {@link OPreparedMail} prepared mail which will be send
     */
    public void sendMailAsync(OPreparedMail mail);

    /**
     * Send mails to recipients asynchronous
     * @param mails {@link List<OPreparedMail>} prepared mails which will be send
     */
    public void sendMailsAsync(List<OPreparedMail> mails);

    /**
     * Send mail to recipient asynchronous and then call callback
     * @param mail {@link OPreparedMail} prepared mail which will be send
     * @param f {@link Consumer<Boolean>} callback which will be called after sending email.
     *                                   Arguments:
     *                                   true - sending mail successful
     *                                   false - sending mail failed
     */
    public void sendMailAsync(OPreparedMail mail, Consumer<Boolean> f);


    /**
     * Fetch mails from mail address. Call callback on each mail
     * Uses IMAP.
     * @param settings {@link OMailSettings} mail settings
     * @param folderName {@link String} folder which contains mails
     * @param consumer {@link Consumer<Message>} callback
     * @throws MessagingException
     */
    public void fetchMails(OMailSettings settings, String folderName, Consumer<Message> consumer) throws MessagingException;

    /**
     * Fetch mails from mail address asynchronous. Call callback on each mail
     * Uses IMAP.
     * @param settings {@link OMailSettings} mail settings
     * @param folderName {@link String} folder which contains mails
     * @param consumer {@link Consumer<Message>} callback
     */
    public CompletableFuture<Void> fetchMailsAsync(OMailSettings settings, String folderName, Consumer<Message> consumer);
}
