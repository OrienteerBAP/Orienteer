package org.orienteer.core.module;

import org.orienteer.core.OrienteerWebApplication;

import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Interface for Orienteer modules
 */
public interface IOrienteerModule
{
	/**
	 * Name of the module. Should be static
	 * @return name of the module
	 */
	public String getName();
	
	/**
	 * Current version of the module
	 * @return a version as number
	 */
	public int getVersion();
	
	/**
	 * Install this application in the environment
	 * @param app {@link OrientDbWebApplication}
	 * @param db database
	 * @return {@link ODocument} for a module or null of default OModule is OK
	 */
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db);
	
	/**
	 * Update installed module
	 * @param app {@link OrientDbWebApplication}
	 * @param db database
	 * @param moduleDoc module {@link ODocument}
	 * @param oldVersion previous version
	 * @param newVersion new version
	 * @return
	 */
	public ODocument onUpdate(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc, int oldVersion, int newVersion);
	
	/**
	 * Uninstall this module
	 * @param app {@link OrientDbWebApplication}
	 * @param db database
	 * @param moduleDoc module {@link ODocument}
	 */
	public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc);
	
	/**
	 * Run this module
	 * @param app {@link OrientDbWebApplication}
	 * @param db database
	 * @param moduleDoc module {@link ODocument}
	 */
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc);
	
	/**
	 * Invoked when module configuration was changed
	 * @param app {@link OrientDbWebApplication}
	 * @param db database
	 * @param moduleDoc module {@link ODocument}
	 */
	public void onConfigurationChange(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc);
	
	/**
	 * Stop this module
	 * @param app {@link OrientDbWebApplication}
	 * @param db database
	 * @param moduleDoc module {@link ODocument}
	 */
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc);
}
