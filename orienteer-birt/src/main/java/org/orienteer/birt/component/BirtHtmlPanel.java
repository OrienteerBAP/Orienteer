package org.orienteer.birt.component;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;

public class BirtHtmlPanel  extends AbstractBirtPanel{



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BirtHtmlPanel(String id,String reportFileName) {
		super(id, reportFileName);
	}
	

	public BirtHtmlPanel(String id, String reportFileName, Map<String, Object> parameters) {
		super(id, reportFileName, parameters);
	}

	public BirtHtmlPanel(String id, InputStream report, Map<String, Object> parameters) {
		super(id, report, parameters);
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
