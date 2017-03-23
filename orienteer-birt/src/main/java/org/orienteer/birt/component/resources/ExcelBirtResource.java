package org.orienteer.birt.component.resources;

import java.io.OutputStream;

import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.orienteer.birt.component.IBirtReportData;

/**
 * BIRT report as xls file
 *
 */
public class ExcelBirtResource extends AbstractBirtResource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExcelBirtResource(IBirtReportData reportData) {
		super(reportData);
	}

	@Override
	protected IRenderOption getRenderOptions(OutputStream output) {
	    
		EXCELRenderOption options = new EXCELRenderOption();
		options.setOutputFormat("xls");
		options.setOutputStream(output);
		return options;
	}

	@Override
	protected void setResourceData(ResourceResponse resourceResponse) {
	    resourceResponse.setContentType("application/vnd.ms-excel");
	}

}
