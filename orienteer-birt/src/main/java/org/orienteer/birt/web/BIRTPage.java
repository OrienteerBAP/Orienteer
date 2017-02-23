package org.orienteer.birt.web;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.orienteer.birt.Module;
import org.orienteer.birt.component.BirtHtmlPanel;
import org.orienteer.birt.component.BirtHtmlResource;
import org.orienteer.birt.component.BirtPaginatedHtmlPanel;
import org.orienteer.core.MountPath;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.web.BasePage;

	@MountPath("/birt")
	public class BIRTPage extends BasePage<Object>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public static final String FROM_HOME_PARAM = "_fh";
		
		public void BIRTPage1() throws EngineException
		{
			Module module = (Module)OrienteerWebApplication.get().getModuleByName("orienteer-birt");
			IReportEngine engine = module.engine;
			
			IReportRunnable design = engine.openReportDesign("temp/remote.rptdesign");
		 
			//Create task to run the report - use the task to execute the report and save to disk.
			IRunTask runTask = engine.createRunTask(design); 
		
			//Set parent classloader for engine
			//task.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, 
			//		RunTaskExample.class.getClassLoader()); 
		     
		//run the report and destroy the engine
			runTask.run("temp/remote.rptdocument");		
			runTask.close();
			IReportDocument document = engine.openReportDocument("temp/remote.rptdocument");
			IRenderTask renderTask = engine.createRenderTask(document);
			
			////////////////////////////////////////////////////////////////
			//IReportDocument document;
			//Setup rendering to HTML
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			HTMLRenderOption options = new HTMLRenderOption();
			options.setOutputFormat("html");
			options.setOutputStream(buf);
			options.setHtmlPagination(true);
			renderTask.setRenderOption(options);
			renderTask.setPageRange("1-5");
			renderTask.setPageNumber(2);
			
			options.setEmbeddable(true);
			//run the report
			renderTask.render();
			document.close();
			
			Label report;
			try {
				report = new Label("report", buf.toString("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				report = new Label("report", buf.toString());
			}
			report.setEscapeModelStrings(false);
			add(report);
		}
		public void BIRTPage2() throws EngineException
		{
			Module module = (Module)OrienteerWebApplication.get().getModuleByName("orienteer-birt");
			IReportEngine engine = module.engine;
			
			//Open the report design
			IReportRunnable design = engine.openReportDesign("temp/remote.rptdesign");
			
			//Create task to run and render the report,
			IRunAndRenderTask task = engine.createRunAndRenderTask(design);
			////////////////////////////////////////////////////////////////
			
			////////////////////////////////////////////////////////////////
			//IReportDocument document;
			//IRenderTask task1 = engine.createRenderTask(document);
			//Setup rendering to HTML
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
				// TODO Auto-generated catch block
				e.printStackTrace();
				report = new Label("report", buf.toString());
			}
			report.setEscapeModelStrings(false);
			add(report);
		}
		
		public BIRTPage() throws EngineException
		{
			
			/*
			new ResourceLink("rssLink", new RSSProducerResource())
			Label report;
			report = new Label("report", new BirtHtmlResource());
			report.setEscapeModelStrings(false);
			add(report);
			*/
			//ResourceModel
			//add(new Label("reportText", new BirtHtmlResource().));
			//add(new ResourceLink("report", new BirtHtmlResource()));

			AjaxLazyLoadPanel panel = new AjaxLazyLoadPanel("report")
			{
			  @Override
			  public Component getLazyLoadComponent(String id)
			  {
			       return new BirtPaginatedHtmlPanel(id,"remote",new HashMap<String,Object>());
			  }
			};
			add(panel);
			//final BirtHtmlPanel birtPanel = new BirtHtmlPanel("report","temp/remote.rptdesign");
			//birtPanel.setOutputMarkupId(true);
			//add(birtPanel);
			Label pager = new Label("pages");
			/*
			AjaxPagingNavigator pager = new AjaxPagingNavigator("pages", birtPanel) {
				private static final long serialVersionUID = 1L;

				@Override
			    protected void onAjaxEvent(AjaxRequestTarget target) {
			        target.add(birtPanel);
			        target.add(this);
			    }
			};
			pager.setOutputMarkupId(true);
			*/
			add(pager);
			
		}		
		@Override
		public boolean isVersioned()
		{
			return false;
		}
	}