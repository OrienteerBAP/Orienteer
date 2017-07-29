package org.orienteer.birt.component;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.orienteer.birt.component.service.IBirtReportConfig;

/**
 * Show BIRT report as plain embedded html
 */
public class BirtHtmlReportPanel  extends AbstractBirtReportPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public BirtHtmlReportPanel(String id, IBirtReportConfig config) throws EngineException {
		super(id, config);
	}


	@Override
	protected IRenderOption makeRenderOption() {
		HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFormat("html");
		options.setHtmlPagination(true);
		options.setEmbeddable(true);
		options.setEnableInlineStyle(true);
		return options;
	}
	
}
