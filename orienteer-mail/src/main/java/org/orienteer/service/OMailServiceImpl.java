package org.orienteer.service;

import org.orienteer.model.OMail;
import org.orienteer.model.OMailSettings;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Implementation of {@link IOMailService}
 */
public class OMailServiceImpl implements IOMailService {

    @Override
    public void sendMail(String to, OMail mail) throws MessagingException, UnsupportedEncodingException {
        final OMailSettings settings = mail.getMailSettings();
        final Session session = createSession(settings);
        final Message message = new MimeMessage(session);
        message.setFrom(createFrom(mail, settings));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(mail.getSubject());
        message.setContent(mail.getText(), "text/html");
        Transport.send(message);
    }

    @Override
    public void sendMailAsync(String to, OMail mail) {
        sendMailAsync(to, mail, null);
    }

    @Override
    public void sendMailAsync(String to, OMail mail, Consumer<Boolean> f) {
        new Thread(() -> {
            boolean success = false;
            try {
                sendMail(to, mail);
                success = true;
            } catch (Exception ex) {
                // Nothing to do
            } finally {
                if (f != null) f.accept(success);
            }
        }).start();
    }

    private InternetAddress createFrom(OMail mail, OMailSettings settings) throws UnsupportedEncodingException {
        return new InternetAddress(settings.getEmail(), mail.getFrom());
    }

    private Session createSession(OMailSettings settings) {
        Properties properties = createProperties(settings);
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(settings.getEmail(), settings.getPassword());
            }
        });
    }

    private Properties createProperties(OMailSettings settings) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", settings.isTlsSsl());
        properties.put("mail.smtp.host", settings.getSmtpHost());
        properties.put("mail.smtp.port", settings.getSmtpPort());
        return properties;
    }
}