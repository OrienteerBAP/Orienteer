package org.orienteer.birt.component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;

/**
 * Show BIRT report as plain embedded html
 */
public class BirtHtmlReportPanel  extends AbstractBirtReportPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BirtHtmlReportPanel(String id, String reportFileName, Map<String, Object> parameters,boolean useLocalDB) throws EngineException, FileNotFoundException {
		super(id, reportFileName, parameters,useLocalDB);
	}

	public BirtHtmlReportPanel(String id, InputStream report, Map<String, Object> parameters,boolean useLocalDB) throws EngineException {
		super(id, report, parameters,useLocalDB);
	}


	@Override
	protected IRenderOption makeRenderOption() {
		HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFormat("html");
		options.setHtmlPagination(true);
		options.setEmbeddable(true);
		return options;
	}
	
}
