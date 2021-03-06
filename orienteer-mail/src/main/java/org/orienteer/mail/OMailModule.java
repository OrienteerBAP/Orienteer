package org.orienteer.mail;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.tasks.IOTask;
import org.orienteer.core.tasks.OTaskSession;
import org.orienteer.mail.model.OMailAttachment;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.mail.model.OMail;
import org.orienteer.mail.model.OMailSettings;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.task.IOSendMailTask;
import org.orienteer.mail.task.OSendMailTaskSession;

import static com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;

/**
 * {@link IOrienteerModule} for 'orienteer-mail' module
 */
public class OMailModule extends AbstractOrienteerModule {

    public static final String NAME = "orienteer-mail";

	protected OMailModule() {
		super(NAME, 7);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);

		helper.oClass(OMailSettings.CLASS_NAME)
				.oProperty(OMailSettings.OPROPERTY_EMAIL, OType.STRING, 0).notNull().markAsDocumentName().oIndex(INDEX_TYPE.UNIQUE)
				.oProperty(OMailSettings.OPROPERTY_PASSWORD, OType.STRING, 10).notNull().assignVisualization("password")
				.oProperty(OMailSettings.OPROPERTY_SMTP_HOST, OType.STRING, 20).notNull()
				.oProperty(OMailSettings.OPROPERTY_SMTP_PORT, OType.INTEGER, 30).notNull()
				.oProperty(OMailSettings.OPROPERTY_IMAP_HOST, OType.STRING, 40).notNull()
				.oProperty(OMailSettings.OPROPERTY_IMAP_PORT, OType.INTEGER, 50).notNull()
				.oProperty(OMailSettings.OPROPERTY_TLS_SSL, OType.BOOLEAN, 60).defaultValue("true").notNull();

        helper.oClass(OMailAttachment.CLASS_NAME)
                .oProperty(OMailAttachment.PROP_NAME, OType.STRING, 0).markAsDocumentName().notNull()
                .oProperty(OMailAttachment.PROP_DATA, OType.BINARY, 10).notNull();

		helper.oClass(OMail.CLASS_NAME)
				.oProperty(OMail.OPROPERTY_NAME, OType.STRING, 0).notNull().markAsDocumentName().oIndex(INDEX_TYPE.UNIQUE)
				.oProperty(OMail.OPROPERTY_SUBJECT, OType.STRING, 10)
				.oProperty(OMail.OPROPERTY_FROM, OType.STRING, 20)
				.oProperty(OMail.OPROPERTY_TEXT, OType.STRING, 30).assignVisualization("html")
		        .oProperty(OMail.PROP_ATTACHMENTS, OType.LINKLIST, 40).linkedClass(OMailAttachment.CLASS_NAME)
				.oProperty(OMail.OPROPERTY_SETTINGS, OType.LINK, 50).linkedClass(OMailSettings.CLASS_NAME).notNull();

		helper.oClass(OPreparedMail.CLASS_NAME)
                .oProperty(OPreparedMail.PROP_NAME, OType.STRING, 0).notNull().markAsDocumentName()
                .oProperty(OPreparedMail.PROP_SUBJECT, OType.STRING, 10).notNull()
                .oProperty(OPreparedMail.PROP_TEXT, OType.STRING, 20).notNull().assignVisualization("html")
                .oProperty(OPreparedMail.PROP_RECIPIENTS, OType.EMBEDDEDLIST, 30).notNull().linkedType(OType.STRING)
                .oProperty(OPreparedMail.PROP_FROM, OType.STRING, 40).notNull()
                .oProperty(OPreparedMail.PROP_BCC, OType.EMBEDDEDLIST, 50).linkedType(OType.STRING)
                .oProperty(OPreparedMail.PROP_ATTACHMENTS, OType.LINKLIST, 60).linkedClass(OMailAttachment.CLASS_NAME)
                .oProperty(OPreparedMail.PROP_MAIL, OType.LINK, 70).notNull().linkedClass(OMail.CLASS_NAME)
                .oProperty(OPreparedMail.PROP_SETTINGS, OType.LINK, 80).notNull().linkedClass(OMailSettings.CLASS_NAME);

		helper.describeAndInstallSchema(IOSendMailTask.class);

		helper.oClass(OSendMailTaskSession.CLASS_NAME, OTaskSession.TASK_SESSION_CLASS)
                .oProperty(OSendMailTaskSession.PROP_MAILS, OType.LINKLIST).linkedClass(OPreparedMail.CLASS_NAME);

		return null;
	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseSession db, int oldVersion, int newVersion) {
		onInstall(app, db);
	}
}
