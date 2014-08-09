package ru.ydn.orienteer.modules;

import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public interface IOrienteerModule
{
	public String getName();
	public int getVersion();
	public void onInstall(ODatabaseDocument db);
	public void onUpdate(ODatabaseDocument db, int oldVersion, int newVersion);
	public void onUninstall(ODatabaseDocument db);
	
	public void onInitialize(OrientDbWebApplication app, ODatabaseDocument db);
	public void onDestroy(OrientDbWebApplication app, ODatabaseDocument db);
}
