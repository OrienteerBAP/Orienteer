package org.orienteer.birt.component;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.wicket.request.resource.AbstractResource;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.orienteer.birt.Module;
import org.orienteer.core.OrienteerWebApplication;

public class BirtHtmlResource extends AbstractResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//public BirtHtmlResource() {
		// TODO Auto-generated constructor stub
	//}

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
	    ResourceResponse resourceResponse = new ResourceResponse();
	    resourceResponse.setContentType("text/html");
	    resourceResponse.setTextEncoding("utf-8");
	    
	    resourceResponse.setWriteCallback(new WriteCallback()
	    {
	      @Override
	      public void writeData(Attributes attributes) throws  IOException
	      {
	        OutputStream outputStream = attributes.getResponse().getOutputStream();
	        
			Module module = (Module)OrienteerWebApplication.get().getModuleByName("orienteer-birt");
			IReportEngine engine = module.getEngine();
			
			//Open the report design
			IReportRunnable design;
			try {
				design = engine.openReportDesign("temp/remote.rptdesign");
				//Create task to run and render the report,
				IRunAndRenderTask task = engine.createRunAndRenderTask(design);
				////////////////////////////////////////////////////////////////
				
				HTMLRenderOption options = new HTMLRenderOption();
				options.setOutputFormat("html");
				options.setEmbeddable(true);
				options.setOutputStream(outputStream);
				options.setHtmlPagination(false);
				task.setRenderOption(options);
				task.run();
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }      
	    });
	    
	    return resourceResponse;
	}

}
