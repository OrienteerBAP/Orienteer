package org.orienteer.modules;

import org.orienteer.CustomAttributes;
import org.orienteer.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class AbstractOrienteerModule implements IOrienteerModule
{
	private final String name;
	private final int version;
	
	protected AbstractOrienteerModule(String name, int version)
	{
		this.name = name;
		this.version = version;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {

	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {

	}

	@Override
	public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db) {

	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {

	}

	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {

	}
}
