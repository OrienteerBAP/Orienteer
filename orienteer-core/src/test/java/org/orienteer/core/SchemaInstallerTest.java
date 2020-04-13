package org.orienteer.core;

import javax.inject.Singleton;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

@Singleton
public class SchemaInstallerTest extends AbstractOrienteerModule
{
	private static final String TEST_OCLASS="TestSchemaClass";
	
	public SchemaInstallerTest()
	{
		super("test-schema", 1);
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(TEST_OCLASS);
		UIVisualizersRegistry registry = app.getUIVisualizersRegistry();
		for(OType type: OType.values())
		{
			if(type == OType.LINKBAG) continue;
			helper.oProperty(type.name().toLowerCase(), type);
			if(type.isLink()) helper.linkedClass(TEST_OCLASS);
			for(String vizualization : registry.getComponentsOptions(type))
			{
				helper.oProperty(type.name().toLowerCase()+vizualization, type).assignVisualization(vizualization);
				if(type.isLink()) helper.linkedClass(TEST_OCLASS);
			}
		}
	}

}
