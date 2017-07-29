package org.orienteer.birt.component.resources;

import java.io.OutputStream;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.orienteer.birt.component.IBirtReportData;

/**
 * BIRT report as dedicated HTML page
 *
 */
public class HtmlBirtResource extends AbstractBirtResource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HtmlBirtResource(IBirtReportData reportData) {
		super(reportData);
	}

	@Override
	protected IRenderOption getRenderOptions(OutputStream output) {
		HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFormat("html");
		options.setEmbeddable(false);
		options.setHtmlPagination(false);
		options.setOutputStream(output);
		options.setImageHandler(getReportData().getIHTMLImageHandler());
		return options;
	}

	@Override
	protected void setResourceData(ResourceResponse resourceResponse) {
	    resourceResponse.setContentType("text/html");
	    resourceResponse.setTextEncoding("utf-8");
		resourceResponse.setFileName(getReportData().getOutName()+".html");
	}


}
