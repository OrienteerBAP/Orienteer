package org.orienteer.birt.component;

import java.io.OutputStream;

import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

public class PDFBirtResource extends AbstractBirtResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PDFBirtResource(AbstractBirtReportPanel reportPanel) {
		super(reportPanel);
	}

	@Override
	protected IRenderOption getRenderOptions(OutputStream output) {
	    //resourceResponse.setTextEncoding("utf-8");
	    
		PDFRenderOption options = new PDFRenderOption();
		options.setOutputFormat("pdf");
		options.setOutputStream(output);
		return options;
	}

	@Override
	protected void setResourceData(ResourceResponse resourceResponse) {
	    resourceResponse.setContentType("application/pdf");
		
	}

}
