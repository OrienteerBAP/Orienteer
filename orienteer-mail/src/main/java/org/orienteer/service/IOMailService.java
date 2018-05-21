package org.orienteer.service;

import com.google.inject.ImplementedBy;
import org.orienteer.model.OMail;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service for simple sending email
 */
@ImplementedBy(OMailServiceImpl.class)
public interface IOMailService {

    /**
     * Send mail to recipient
     * @param to {@link String} recipient
     * @param mail {@link OMail} mail which will be send
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public void sendMail(String to, OMail mail) throws MessagingException, UnsupportedEncodingException;

    /**
     * Send mail to recipients
     * @param to {@link List<String>} recipients
     * @param mail {@link OMail} mail which will be send
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public void sendMail(List<String> to, OMail mail) throws MessagingException, UnsupportedEncodingException;

    /**
     * Send mail to recipient asynchronous
     * @param to {@link String} recipient
     * @param mail {@link OMail} mail which will be send
     */
    public void sendMailAsync(String to, OMail mail);

    /**
     * Send mail to recipients asynchronous
     * @param to {@link List<String>} recipients
     * @param mail {@link OMail} mail which will be send
     */
    public void sendMailAsync(List<String> to, OMail mail);

    /**
     * Send mail to recipient asynchronous and then call callback
     * @param to {@link String} recipient
     * @param mail {@link OMail} mail which will be send
     * @param f {@link Consumer<Boolean>} callback which will be called after sending email.
     *                                   Arguments:
     *                                   true - sending mail successful
     *                                   false - sending mail failed
     */
    public void sendMailAsync(String to, OMail mail, Consumer<Boolean> f);

    /**
     * Send mail to recipients asynchronous and then call callback
     * @param to {@link List<String>} recipients
     * @param mail {@link OMail} mail which will be send
     * @param f {@link Consumer<Boolean>} callback which will be called after sending email.
     *                                   Arguments:
     *                                   true - sending mail successful
     *                                   false - sending mail failed
     */
    public void sendMailAsync(List<String> to, OMail mail, Consumer<Boolean> f);
}
