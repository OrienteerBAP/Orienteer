package org.orienteer.service;

import org.apache.wicket.ThreadContext;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.model.OMail;
import org.orienteer.model.OMailSettings;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Implementation of {@link IOMailService}
 */
public class OMailServiceImpl implements IOMailService {

    @Override
    public void sendMail(String to, OMail mail) throws MessagingException, UnsupportedEncodingException {
        sendMail(Collections.singletonList(to), mail);
    }

    @Override
    public void sendMail(List<String> to, OMail mail) throws MessagingException, UnsupportedEncodingException {
        final OMailSettings settings = mail.getMailSettings();
        final Session session = createSession(settings, createSendMailProperties(settings));
        final Message message = new MimeMessage(session);
        message.setFrom(createFrom(mail, settings));
        message.setRecipients(Message.RecipientType.TO, toAddressArray(to));
        message.setSubject(mail.getSubject());
        message.setContent(mail.getText(), "text/html;charset=UTF-8");
        Transport.send(message);
    }

    @Override
    public void sendMailAsync(String to, OMail mail) {
        sendMailAsync(to, mail, null);
    }

    @Override
    public void sendMailAsync(List<String> to, OMail mail) {
        sendMailAsync(to, mail, null);
    }

    @Override
    public void sendMailAsync(String to, OMail mail, Consumer<Boolean> f) {
        sendMailAsync(Collections.singletonList(to), mail, f);
    }

    @Override
    public void sendMailAsync(List<String> to, OMail mail, Consumer<Boolean> f) {
        OrienteerWebSession session = OrienteerWebSession.get();
        OrienteerWebApplication app = OrienteerWebApplication.get();
        RequestCycle requestCycle = RequestCycle.get();
        new Thread(() -> {
            boolean success = false;
            try {
                ThreadContext.setSession(session);
                ThreadContext.setApplication(app);
                ThreadContext.setRequestCycle(requestCycle);
                sendMail(to, mail);
                success = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (f != null) f.accept(success);
            }
        }).start();
    }

    @Override
    public void fetchMails(OMailSettings settings, String folderName, Consumer<Message> consumer) throws MessagingException {
        Session session = createSession(settings, createCheckMailProperties(settings));
        Store store = session.getStore("imaps");
        store.connect();
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_WRITE);
        Message [] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        for (int i = messages.length - 1; i >= 0; i--) {
            Message message = messages[i];
            consumer.accept(message);
            message.setFlag(Flags.Flag.SEEN, true);
        }
        folder.close(false);
        store.close();
    }

    @Override
    public CompletableFuture<Void> fetchMailsAsync(OMailSettings settings, String folderName, Consumer<Message> consumer) {
        OrienteerWebSession session = OrienteerWebSession.get();
        OrienteerWebApplication app = OrienteerWebApplication.get();
        RequestCycle requestCycle = RequestCycle.get();
        return CompletableFuture.runAsync(() -> {
            ThreadContext.setSession(session);
            ThreadContext.setApplication(app);
            ThreadContext.setRequestCycle(requestCycle);
            try {
                fetchMails(settings, folderName, consumer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private InternetAddress createFrom(OMail mail, OMailSettings settings) throws UnsupportedEncodingException {
        return new InternetAddress(settings.getEmail(), mail.getFrom());
    }

    private Session createSession(OMailSettings settings, Properties properties) {
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(settings.getEmail(), settings.getPassword());
            }
        });
    }

    private Properties createSendMailProperties(OMailSettings settings) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", settings.isTlsSsl());
        properties.put("mail.smtp.host", settings.getSmtpHost());
        properties.put("mail.smtp.port", settings.getSmtpPort());
        return properties;
    }

    private Properties createCheckMailProperties(OMailSettings settings) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", settings.getImapHost());
        properties.put("mail.imaps.port", settings.getImapPort());
        return properties;
    }

    private Address [] toAddressArray(List<String> recipients) throws AddressException {
        Address[] addresses = new Address[recipients.size()];
        for (int i = 0; i < recipients.size(); i++) {
            addresses[i] = new InternetAddress(recipients.get(i));
        }
        return addresses;
    }
}
