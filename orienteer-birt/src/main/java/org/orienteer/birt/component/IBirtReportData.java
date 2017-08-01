package org.orienteer.birt.component;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IHTMLImageHandler;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;

/**
 * BIRT report data interface for BIRT report resources
 * 
 */
public interface IBirtReportData {
	public IReportDocument getReportCache() throws EngineException;
	public IReportEngine getReportEngine();
	public IHTMLImageHandler getIHTMLImageHandler();
	public String getOutName();
}
