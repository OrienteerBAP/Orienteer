package org.orienteer.birt.component;

import java.io.InputStream;
import java.util.Map;

import org.apache.wicket.markup.html.panel.Panel;
import org.eclipse.birt.report.engine.api.EngineException;

/**
 * BirtHemlPanel with paginator
 */
public class BirtManagedHtmlReportPanel extends Panel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String MANAGEMENT_PANEL_NAME = "managementPanel";
	private static final String REPORT_PANEL_NAME = "report";
	
	
	public BirtManagedHtmlReportPanel(String id,BirtReportConfig config) throws EngineException {
		super(id);
		final BirtHtmlReportPanel birtPanel = new BirtHtmlReportPanel(REPORT_PANEL_NAME,config);
		birtPanel.setOutputMarkupId(true);
		add(birtPanel);
		
		BirtManagementPanel managementPanel = new BirtManagementPanel(MANAGEMENT_PANEL_NAME,birtPanel);
		add(managementPanel);
	}

}
