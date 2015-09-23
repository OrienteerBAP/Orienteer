package org.orienteer.graph.module;

import com.google.inject.Singleton;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.component.visualizer.PasswordVisualizer;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.service.OrienteerModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.type.tree.provider.OMVRBTreeRIDProvider;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

/**
 * {@link AbstractOrienteerModule} to provide graph extentions
 */
@Singleton
public class GraphModule extends AbstractOrienteerModule {

	private static OrientGraphFactory graphFactory;

	protected GraphModule() {
		super("graph", 1);

		OrienteerWebApplication.get().registerWidgets("org.orienteer.graph.component.widget");
	}
	
	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		onUpdate(app, db, 0, getVersion());
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		IOrientDbSettings dbSettings = OrienteerWebApplication.get().getOrientDbSettings();
		graphFactory = new OrientGraphFactory(dbSettings.getDBUrl(), dbSettings.getDBUserName(), dbSettings.getDBUserPassword());
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
		if(oldVersion+1<newVersion) onUpdate(app, db, oldVersion + 1, newVersion);
	}

	@Override
	public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db) {
		if (graphFactory != null)
			graphFactory.close();
	}

	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		if (graphFactory != null)
			graphFactory.close();
	}

	public static OrientGraphFactory getGraphFactory() {
		return graphFactory;
	}

	public void onUpdateToFirstVesion(OrienteerWebApplication app, ODatabaseDocument db)
	{
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass("V")
			  .oClass("E");
	}

}
