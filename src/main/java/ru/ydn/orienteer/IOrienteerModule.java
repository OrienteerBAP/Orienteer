package ru.ydn.orienteer;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public interface IOrienteerModule
{
	public String getName();
	public int getVersion();
	public void onInstall(ODatabaseDocument db);
	public void onUpdate(ODatabaseDocument db, int oldVersion, int newVersion);
	public void onUninstall(ODatabaseDocument db);
}
