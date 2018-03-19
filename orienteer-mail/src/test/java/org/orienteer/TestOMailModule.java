package org.orienteer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.model.OMail;
import org.orienteer.model.OMailSettings;
import org.orienteer.service.IOMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for sending email ignored, because don't set tested mail account.
 */
@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestOMailModule
{
	private static final Logger LOG = LoggerFactory.getLogger(TestOMailModule.class);

	@Inject
	private OrienteerTester tester;

	@Inject
	private IOMailService mailService;

	private OMail mail;
	private String to;

	@Before
	public void init() {
		String email = "test@gmail.com";
		String password = "qwerty";
		String mailName = "test";
		to = "to@gmail.com";
		Function<String, OMail> query = (name) -> {
			List<ODocument> docs  = OrienteerWebSession.get().getDatabase().query(new OSQLSynchQuery<>("select from "
					+ OMail.CLASS_NAME + " where " + OMail.NAME + " = ?", 1), name);
			return docs != null && !docs.isEmpty() ? new OMail(docs.get(0)) : null;
		};
		Consumer<String> create = (name) -> {
			OMailSettings settings = new OMailSettings()
					.setEmail(email)
					.setPassword(password)
					.setSmtpHost("smtp.gmail.com")
					.setSmtpPort(587)
					.setTlsSsl(true);
			OMail mail = new OMail()
					.setMailSettings(settings)
					.setName(name)
					.setFrom("orienteer-mail-test")
					.setSubject("Test module 'orienteer-mail'")
					.setText("<h1>Hello, World!</h1>");
			settings.sudoSave();
			mail.sudoSave();
		};
		create.accept(mailName);
		mail = query.apply(mailName);
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
	public void testModuleLoaded()
	{
	    OrienteerWebApplication app = tester.getApplication();
	    assertNotNull(app);
	    IOrienteerModule module = app.getModuleByName("orienteer-mail");
	    assertNotNull(module);
	    assertTrue(module instanceof OMailModule);
	}

	@Test
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
	public void testSendEmailAsyncWithCallback() throws InterruptedException {
		mailService.sendMailAsync(to, mail, Assert::assertTrue);
		Thread.currentThread().join(10_000);
	}
}
