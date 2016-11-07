package org.orienteer.core.module;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Abstract dummy {@link IOrienteerModule} to simplify creation of specific Orienteer modules
 */
public abstract class AbstractOrienteerModule implements IOrienteerModule
{
	private final String name;
	private final int version;
	private final Set<String> dependencies;
	
	protected AbstractOrienteerModule(String name, int version) {
		this(name, version, new HashSet<String>());
	}
	
	protected AbstractOrienteerModule(String name, int version, String... dependencies) {
		this(name, version, new HashSet<String>(Arrays.asList(dependencies)));
	}
	
	protected AbstractOrienteerModule(String name, int version, Set<String> dependencies)
	{
		this.name = name;
		this.version = version;
		this.dependencies = Collections.unmodifiableSet(dependencies);
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
	public Set<String> getDependencies() {
		return dependencies;
	}

	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		return null;
	}
	
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {
		
	}

	@Override
	public ODocument onUpdate(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc,
			int oldVersion, int newVersion) {
		onUpdate(app, db, oldVersion, newVersion);
		return moduleDoc;
	}
	
	public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db) {
		
	}

	@Override
	public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
		onUninstall(app, db);
	}
	
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
		onInitialize(app, db);
	}
	
	@Override
	public void onConfigurationChange(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
		
	}
	
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		
	}

	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
		onDestroy(app, db);
	}
}
