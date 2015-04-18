package org.orienteer.core.module;

import org.orienteer.core.OrienteerWebApplication;

import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

public interface IOrienteerModule
{
	public String getName();
	public int getVersion();
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db);
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion);
	public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db);
	
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db);
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db);
}
