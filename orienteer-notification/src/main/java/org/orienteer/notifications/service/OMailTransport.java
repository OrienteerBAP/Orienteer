package org.orienteer.notifications.service;

import org.orienteer.mail.model.OMailAttachment;
import org.orienteer.mail.model.OMailSettings;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.notifications.model.OMailNotification;
import org.orienteer.notifications.model.OMailNotificationTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

/**
 * Transport for send notification throughout mail
 */
public class OMailTransport implements ITransport<OMailNotification> {

  private static final Logger LOG = LoggerFactory.getLogger(OMailTransport.class);

  public static final int CONNECTION_ATTEMPTS = 10;

  private final OMailSettings settings;
  private final Session session;
  private final Transport transport;

  public OMailTransport(OMailNotificationTransport transport) {
    settings = transport.getMailSettings();
    session = createSession(settings, createSendMailProperties(settings));

    try {
      this.transport = session.getTransport("smtp");
      connect();
    } catch (Exception e) {
      throw new IllegalStateException("Can't create inner transport for: " + transport, e);
    }
  }

  @Override
  public void send(OMailNotification notification) {
    LOG.info("Send mail notification: {} {}", notification.getDescription(), transport.isConnected());
    connect();

    final Message message = new MimeMessage(session);
    OPreparedMail mail = notification.getPreparedMail();

    try {
      Address[] recipients = toAddressArray(mail.getRecipients());
      message.setRecipients(Message.RecipientType.TO, recipients);
      message.setRecipients(Message.RecipientType.BCC, toAddressArray(mail.getBcc()));

      message.setFrom(createFrom(mail, settings));
      message.setSubject(mail.getSubject());
      message.setContent(createMessageContent(mail));
      transport.sendMessage(message, recipients);
    } catch (Exception e) {
      throw new IllegalStateException("Can't send notification: " + notification, e);
    }
  }

  @Override
  public void close() throws IOException {
    try {
      if (transport.isConnected()) {
        transport.close();
      }
    } catch (MessagingException e) {
      LOG.error("Can't close inner transport: {}", transport, e);
    }
  }

  private void connect() {
    synchronized (this) {
      if (!transport.isConnected()) {
        for (int i = 1; i <= CONNECTION_ATTEMPTS; i++) {
          try {
            this.transport.connect();
            Thread.sleep(500);
            break;
          } catch (MessagingException e) {
            if (i == CONNECTION_ATTEMPTS) {
              throw new IllegalStateException(e);
            }
          } catch (InterruptedException e) {}
        }
      }
    }
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


  private Address [] toAddressArray(List<String> recipients) throws AddressException {
    Address[] addresses = new Address[recipients.size()];
    for (int i = 0; i < recipients.size(); i++) {
      addresses[i] = new InternetAddress(recipients.get(i));
    }
    return addresses;
  }

  private Properties createSendMailProperties(OMailSettings settings) {
    Properties properties = new Properties();
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", settings.isTlsSsl());
    properties.put("mail.smtp.host", settings.getSmtpHost());
    properties.put("mail.smtp.port", settings.getSmtpPort());
    return properties;
  }

  protected Session createSession(OMailSettings settings, Properties properties) {
    return Session.getInstance(properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(settings.getEmail(), settings.getPassword());
      }
    });
  }
}
