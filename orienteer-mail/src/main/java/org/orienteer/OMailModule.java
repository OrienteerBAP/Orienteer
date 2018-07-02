package org.orienteer;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.model.OMail;
import org.orienteer.model.OMailSettings;

import static com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;

/**
 * {@link IOrienteerModule} for 'orienteer-mail' module
 */
public class OMailModule extends AbstractOrienteerModule{

	protected OMailModule() {
		super("orienteer-mail", 4);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
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

		helper.oClass(OMail.CLASS_NAME)
				.oProperty(OMail.OPROPERTY_NAME, OType.STRING, 0).notNull().markAsDocumentName().oIndex(INDEX_TYPE.UNIQUE)
				.oProperty(OMail.OPROPERTY_SUBJECT, OType.STRING, 10)
				.oProperty(OMail.OPROPERTY_FROM, OType.STRING, 20)
				.oProperty(OMail.OPROPERTY_TEXT, OType.STRING, 30).assignVisualization("html")
				.oProperty(OMail.OPROPERTY_SETTINGS, OType.LINK, 40).linkedClass(OMailSettings.CLASS_NAME).notNull();
		return null;
	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
		onInstall(app, db);
	}
}
