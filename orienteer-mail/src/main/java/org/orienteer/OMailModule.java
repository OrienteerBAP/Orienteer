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
		super("orienteer-mail", 3);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);

		helper.oClass(OMailSettings.CLASS_NAME)
				.oProperty(OMailSettings.EMAIL, OType.STRING, 0).notNull().markAsDocumentName().oIndex(INDEX_TYPE.UNIQUE)
				.oProperty(OMailSettings.PASSWORD, OType.STRING, 10).notNull().assignVisualization("password")
				.oProperty(OMailSettings.SMTP_HOST, OType.STRING, 20).notNull()
				.oProperty(OMailSettings.SMTP_PORT, OType.INTEGER, 30).notNull()
				.oProperty(OMailSettings.TLS_SSL, OType.BOOLEAN, 40).defaultValue("true").notNull();

		helper.oClass(OMail.CLASS_NAME)
				.oProperty(OMail.NAME, OType.STRING, 0).notNull().markAsDocumentName().oIndex(INDEX_TYPE.UNIQUE)
				.oProperty(OMail.SUBJECT, OType.STRING, 10)
				.oProperty(OMail.FROM, OType.STRING, 20)
				.oProperty(OMail.TEXT, OType.STRING, 30).assignVisualization("html")
				.oProperty(OMail.SETTINGS, OType.LINK, 40).notNull();
		return null;
	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
		onInstall(app, db);
	}
}
