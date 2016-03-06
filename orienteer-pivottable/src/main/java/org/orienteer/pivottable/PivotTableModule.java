package org.orienteer.pivottable;

import org.orienteer.core.CustomAttributes;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'orienteer-pivottable' module
 */
public class PivotTableModule extends AbstractOrienteerModule{

	protected PivotTableModule() {
		super("orienteer-pivottable", 1);
	}
	
	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		//Install data model
	}
	
}
