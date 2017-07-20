package org.orienteer.birt.component.resources;

import java.io.OutputStream;

import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.orienteer.birt.component.IBirtReportData;

/**
 * BIRT report as PDF file
 *
 */
public class PDFBirtResource extends AbstractBirtResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PDFBirtResource(IBirtReportData reportData) {
		super(reportData);
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
		resourceResponse.setFileName(getReportData().getOutName()+".pdf");
		
	}

}
