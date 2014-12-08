package ru.ydn.orienteer;

import java.util.List;

import javax.inject.Singleton;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

import ru.ydn.orienteer.components.properties.UIComponentsRegistry;
import ru.ydn.orienteer.modules.AbstractOrienteerModule;
import ru.ydn.orienteer.utils.OSchemaHelper;

@Singleton
public class TestSchemaInstaller extends AbstractOrienteerModule
{
	private static final String TEST_OCLASS="TestSchemaClass";
	
	public TestSchemaInstaller()
	{
		super("test-schema", 1);
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(TEST_OCLASS);
		UIComponentsRegistry registry = app.getUIComponentsRegistry();
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
