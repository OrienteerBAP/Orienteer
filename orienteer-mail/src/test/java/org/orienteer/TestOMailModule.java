package org.orienteer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.junit.*;
import org.junit.runner.RunWith;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.model.OMail;
import org.orienteer.model.OMailSettings;
import org.orienteer.model.OPreparedMail;
import org.orienteer.service.IOMailService;
import org.orienteer.util.OMailUtils;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for sending email ignored, because don't set tested mail account.
 */
@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestOMailModule {
	@Inject
	private OrienteerTester tester;

	@Inject
	private IOMailService mailService;

	private OMail mail;
	private OMailSettings settings;
	private String to;

	@Before
	public void init() {
		String email = "test@gmail.com";
		String password = "qwerty";
		String mailName = "test";
		to = "to@gmail.com";

		settings = new OMailSettings()
				.setEmail(email)
				.setPassword(password)
				.setSmtpHost("smtp.gmail.com")
				.setSmtpPort(587)
				.setImapHost("imap.gmail.com")
				.setImapPort(993)
				.setTlsSsl(true);
		mail = new OMail()
				.setMailSettings(settings)
				.setName(mailName)
				.setFrom("orienteer-mail-test")
				.setSubject("Test module 'orienteer-mail'")
				.setText("<h1>Hello, World!</h1>");

		DBClosure.sudoSave(settings, mail);

		mail = OMailUtils.getOMailByName(mailName).orElseThrow(IllegalAccessError::new);
		assertNotNull(mail);
	}

	@After
	public void destroy() {
		new DBClosure<Void>() {
			@Override
			protected Void execute(ODatabaseDocument db) {
				db.command(new OCommandSQL("delete from ?")).execute(mail.getDocument());
				db.command(new OCommandSQL("delete from ?")).execute(mail.getMailSettings().getDocument());
				return null;
			}
		}.execute();
	}

	@Test
	public void testModuleLoaded() {
	    OrienteerWebApplication app = tester.getApplication();
	    assertNotNull(app);
	    IOrienteerModule module = app.getModuleByName("orienteer-mail");
	    assertNotNull(module);
	    assertTrue(module instanceof OMailModule);
	}

	@Test
	@Ignore
	public void testSendEmail() {
		try {
            OPreparedMail preparedMail = new OPreparedMail(mail);
            preparedMail.setRecipients(Collections.singletonList(to));
			mailService.sendMail(preparedMail);
		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	public void testMacros() {
		String str = "Hello, World!";
		Map<String, Object> macros = new HashMap<>(1);
		macros.put("test", str);
		assertTrue(OMailUtils.applyMacros("${test}", macros).equals(str));
	}

	@Test
	@Ignore
	public void testCheckEmailAsync() throws InterruptedException {
        mailService.fetchMailsAsync(settings, "inbox", message -> {
            try {
                InternetAddress from = (InternetAddress) message.getFrom()[0];
                String subject = message.getSubject();
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                assertNotNull(from.getAddress());
                assertNotNull(subject);
				for (int i = 0; i < mimeMultipart.getCount(); i++) {
					BodyPart part = mimeMultipart.getBodyPart(i);
					assertNotNull(part.getContent());
				}
            } catch (MessagingException | IOException e) {
                throw new IllegalStateException(e);
            }
		});
        Thread.currentThread().join(10_000);
	}

	@Test
	@Ignore
	public void testSendEmailAsyncWithCallback() throws InterruptedException {
		OPreparedMail preparedMail = new OPreparedMail(mail);
		preparedMail.setRecipients(Collections.singletonList(to));
		mailService.sendMailAsync(preparedMail, Assert::assertTrue);
		Thread.currentThread().join(10_000);
	}
}
