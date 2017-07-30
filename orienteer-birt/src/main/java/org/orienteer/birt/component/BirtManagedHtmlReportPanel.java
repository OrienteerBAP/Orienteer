package org.orienteer.birt.component;

import org.apache.wicket.markup.html.panel.Panel;
import org.eclipse.birt.report.engine.api.EngineException;
import org.orienteer.birt.component.service.IBirtReportConfig;

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
	
	
	public BirtManagedHtmlReportPanel(String id,IBirtReportConfig config) throws EngineException {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		final BirtHtmlReportPanel birtPanel = new BirtHtmlReportPanel(REPORT_PANEL_NAME,config);
		birtPanel.setOutputMarkupId(true);
		add(birtPanel,
			new BirtManagementPanel(MANAGEMENT_PANEL_NAME,birtPanel));
	}

}
