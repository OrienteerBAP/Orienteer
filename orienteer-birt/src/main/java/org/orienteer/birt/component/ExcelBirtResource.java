package org.orienteer.birt.component;

import java.io.OutputStream;

import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;

public class ExcelBirtResource extends AbstractBirtResource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExcelBirtResource(AbstractBirtReportPanel reportPanel) {
		super(reportPanel);
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
