package org.orienteer.birt.component;

import java.io.OutputStream;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;

public class HtmlBirtResource extends AbstractBirtResource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HtmlBirtResource(AbstractBirtReportPanel reportPanel) {
		super(reportPanel);
	}

	@Override
	protected IRenderOption getRenderOptions(OutputStream output) {
		HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFormat("html");
		options.setEmbeddable(true);
		options.setHtmlPagination(false);
		options.setOutputStream(output);
		return options;
	}

	@Override
	protected void setResourceData(ResourceResponse resourceResponse) {
	    resourceResponse.setContentType("text/html");
	    resourceResponse.setTextEncoding("utf-8");
	}


}
