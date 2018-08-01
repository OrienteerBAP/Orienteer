package org.orienteer;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.model.OMail;
import org.orienteer.model.OMailSettings;
import org.orienteer.model.OPreparedMail;
import org.orienteer.service.IOMailService;
import org.orienteer.service.OMailServiceTest;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(OrienteerTestRunner.class)
public class TestSendMail {

    private OMailServiceTest testService;

    private OMail mail;
    private OPreparedMail preparedMail;

    @Before
    public void init() {
        testService = (OMailServiceTest) OrienteerWebApplication.lookupApplication().getServiceInstance(IOMailService.class);
        OMailSettings mailSettings = createMailSettings();
        DBClosure.sudoSave(mailSettings);

        mail = createMail(mailSettings);
        DBClosure.sudoSave(mail);

        preparedMail = prepareMail(mail);
        DBClosure.sudoSave(preparedMail);
    }

    @After
    public void destroy() {
        testService.onSendMail(null);
        DBClosure.sudoConsumer(db -> {
            OMailSettings settings = mail.getMailSettings();
            db.command(new OCommandSQL("delete from ?")).execute(preparedMail.getDocument());
            db.command(new OCommandSQL("delete from ?")).execute(mail.getDocument());
            db.command(new OCommandSQL("delete from ?")).execute(settings.getDocument());
        });
    }

    @Test
    public void testSendSingleMail() throws UnsupportedEncodingException, MessagingException {
        testService.onSendMail(mail -> assertEquals(preparedMail, mail))
                .sendMail(preparedMail);
    }

    private OMailSettings createMailSettings() {
        OMailSettings settings = new OMailSettings();
        return settings.setEmail(UUID.randomUUID().toString() + "@gmail.com")
                .setPassword(UUID.randomUUID().toString())
                .setImapHost("test")
                .setImapPort(123)
                .setSmtpHost("test")
                .setSmtpPort(123)
                .setTlsSsl(true);
    }

    private OMail createMail(OMailSettings settings) {
        return new OMail()
                .setFrom("test")
                .setMailSettings(settings)
                .setText("<p>${text}</p>")
                .setSubject("${subject}")
                .setName(UUID.randomUUID().toString());
    }

    private OPreparedMail prepareMail(OMail mail) {
        Map<Object, Object> macros = new HashMap<>();
        macros.put("text", "Test text");
        macros.put("subject", "Test subject");
        return new OPreparedMail(mail, macros)
                .setRecipients(Collections.singletonList(UUID.randomUUID().toString() + "@gmail.com"));
    }
}
