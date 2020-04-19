package org.orienteer.core.module;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		return null;
	}
	
	public void onUpdate(OrienteerWebApplication app, ODatabaseSession db,
			int oldVersion, int newVersion) {
		
	}

	@Override
	public ODocument onUpdate(OrienteerWebApplication app, ODatabaseSession db, ODocument moduleDoc,
			int oldVersion, int newVersion) {
		onUpdate(app, db, oldVersion, newVersion);
		return moduleDoc;
	}
	
	public void onUninstall(OrienteerWebApplication app, ODatabaseSession db) {
		
	}

	@Override
	public void onUninstall(OrienteerWebApplication app, ODatabaseSession db, ODocument moduleDoc) {
		onUninstall(app, db);
	}
	
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
		
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db, ODocument moduleDoc) {
		onInitialize(app, db);
	}
	
	@Override
	public void onConfigurationChange(OrienteerWebApplication app, ODatabaseSession db, ODocument moduleDoc) {
		
	}
	
	public void onDestroy(OrienteerWebApplication app, ODatabaseSession db) {
		
	}

	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseSession db, ODocument moduleDoc) {
		onDestroy(app, db);
	}
}
