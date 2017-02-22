package org.orienteer.birt.component;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.orienteer.birt.Module;
import org.orienteer.core.OrienteerWebApplication;

public class BirtHtmlPanel  extends Panel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BirtHtmlPanel(String id) throws EngineException {
		super(id);
		Module module = (Module)OrienteerWebApplication.get().getModuleByName("orienteer-birt");
		IReportEngine engine = module.engine;
		
		//Open the report design
		IReportRunnable design = engine.openReportDesign("temp/remote.rptdesign");
		
		//Create task to run and render the report,
		IRunAndRenderTask task = engine.createRunAndRenderTask(design);
		////////////////////////////////////////////////////////////////
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFormat("html");
		options.setOutputStream(buf);
		options.setHtmlPagination(false);
		task.setRenderOption(options);
		
		options.setEmbeddable(true);
		//run the report
		task.run();
		
		Label report;
		try {
			report = new Label("report", buf.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			report = new Label("report", buf.toString());
		}
		report.setEscapeModelStrings(false);
		add(report);
	}
	
}
