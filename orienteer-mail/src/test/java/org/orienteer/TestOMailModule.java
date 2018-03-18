package org.orienteer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.junit.*;
import org.junit.runner.RunWith;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.model.OMail;
import org.orienteer.model.OMailSettings;
import org.orienteer.service.IOMailService;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for sending email ignored, because don't set tested mail account.
 */
@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestOMailModule
{
	@Inject
	private OrienteerTester tester;

	@Inject
	private IOMailService mailService;

	private OMail mail;
	private String to;

	@Before
	public void init() {
		OMailSettings settings = new OMailSettings()
				.setEmail("")
				.setPassword("")
				.setSmtpHost("smtp.gmail.com")
				.setSmtpPort(587)
				.setTlsSsl(true);
		mail = new OMail()
				.setMailSettings(settings)
				.setName("test")
				.setFrom("orienteer-mail-test")
				.setSubject("Test module 'orienteer-mail'")
				.setText("<h1>Hello, World!</h1>");
		to = "vitaliy.gonchar.work@gmail.com";
	}

	@After
	public void destroy() {
		mail = null;
	}

	@Test
	public void testModuleLoaded()
	{
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
			mailService.sendMail(to, mail);
		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	public void testMacros() {
		String str = "Hello, World!";
		Map<Object, Object> macros = new HashMap<>(1);
		macros.put("test", str);
		OMail mail = new OMail().setText("${test}");
		mail.setMacros(macros);
		assertTrue(mail.getText().equals(str));
	}

	@Test
	@Ignore
	public void testSendEmailAsyncWithCallback() throws InterruptedException {
		mailService.sendMailAsync(to, mail, Assert::assertTrue);
		Thread.currentThread().join(10_000);
	}
}
