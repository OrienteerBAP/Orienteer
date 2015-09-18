package org.orienteer.graph.module;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.PasswordVisualizer;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.service.OrienteerModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.type.tree.provider.OMVRBTreeRIDProvider;

/**
 * {@link AbstractOrienteerModule} to provide graph extentions
 */
public class GraphModule extends AbstractOrienteerModule {

	protected GraphModule() {
		super("graph", 1);
	}
	
	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		onUpdate(app, db, 0, getVersion());
	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {
		if(oldVersion>=newVersion) return;
		switch (oldVersion+1)
		{
			case 1:
				onUpdateToFirstVesion(app, db);
				break;
			default:
				break;
		}
		if(oldVersion+1<newVersion) onUpdate(app, db, oldVersion+1, newVersion);
	}
	
	public void onUpdateToFirstVesion(OrienteerWebApplication app, ODatabaseDocument db)
	{
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass("V")
			  .oClass("E");
	}

}
