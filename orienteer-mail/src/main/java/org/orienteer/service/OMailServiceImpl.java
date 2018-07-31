package org.orienteer.service;

import org.apache.wicket.ThreadContext;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.model.OMailAttachment;
import org.orienteer.model.OMailSettings;
import org.orienteer.model.OPreparedMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.FlagTerm;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Implementation of {@link IOMailService}
 */
public class OMailServiceImpl implements IOMailService {

    private static final Logger LOG = LoggerFactory.getLogger(OMailServiceImpl.class);


    @Override
    public void sendMail(OPreparedMail mail) throws MessagingException, UnsupportedEncodingException {
        final OMailSettings settings = mail.getMailSettings();
        final Session session = createSession(settings, createSendMailProperties(settings));
        final Message message = new MimeMessage(session);
        message.setRecipients(Message.RecipientType.TO, toAddressArray(mail.getRecipients()));
        message.setRecipients(Message.RecipientType.BCC, toAddressArray(mail.getBcc()));

        message.setFrom(createFrom(mail, settings));
        message.setSubject(mail.getSubject());
        message.setContent(createMessageContent(mail));
        Transport.send(message);
    }

    @Override
    public void sendMails(List<OPreparedMail> mails) throws MessagingException, UnsupportedEncodingException {
        for (OPreparedMail mail : mails) {
            sendMail(mail);
        }
    }

    @Override
    public void sendMailAsync(OPreparedMail mail) {
        sendMailAsync(mail, null);
    }

    @Override
    public void sendMailsAsync(List<OPreparedMail> mails) {
        executeInNewThread(() -> {
            try {
                sendMails(mails);
            } catch (Exception e) {
                LOG.error("Error occurred during sending mails: {}", mails, e);
            }
        });
    }

    @Override
    public void sendMailAsync(OPreparedMail mail, Consumer<Boolean> f) {
        executeInNewThread(() -> {
            boolean success = false;
            try {
                sendMail(mail);
                success = true;
            } catch (Exception e) {
                LOG.error("Error occurred during sending mail: {}", mail, e);
            } finally {
                if (f != null) f.accept(success);
            }
        });
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
                LOG.error("Error during fetching mails: {}", settings, ex);
            }
        });
    }

    private Multipart createMessageContent(OPreparedMail mail) throws MessagingException {
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(createTextPart(mail));
        addMailAttachments(multipart, mail);
        return multipart;
    }

    private void addMailAttachments(Multipart multipart, OPreparedMail mail) throws MessagingException {
        List<OMailAttachment> attachments = mail.getAttachments();
        if (!attachments.isEmpty()) {
            for (OMailAttachment attachment : attachments) {
                multipart.addBodyPart(createDataPart(attachment.toDataSource()));
            }
        }
    }

    private MimeBodyPart createTextPart(OPreparedMail mail) throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(mail.getText(), "text/html;charset=UTF-8");
        return bodyPart;
    }

    private BodyPart createDataPart(DataSource dataSource) throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setDataHandler(new DataHandler(dataSource));
        bodyPart.setFileName(dataSource.getName());
        return bodyPart;
    }

    private InternetAddress createFrom(OPreparedMail mail, OMailSettings settings) throws UnsupportedEncodingException {
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

    private void executeInNewThread(Runnable runnable) {
        OrienteerWebSession session = OrienteerWebSession.get();
        OrienteerWebApplication app = OrienteerWebApplication.get();
        RequestCycle requestCycle = RequestCycle.get();

        new Thread(() -> {
            ThreadContext.setSession(session);
            ThreadContext.setApplication(app);
            ThreadContext.setRequestCycle(requestCycle);
            runnable.run();
        }).start();
    }
}
