package org.orienteer.birt.component;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.eclipse.birt.data.engine.executor.cache.Md5Util;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.orienteer.birt.Module;
import org.orienteer.core.OrienteerWebApplication;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public abstract class AbstractBirtPanel extends Panel implements IPageable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String reportComponentName = "report";

	protected static final String reportExtencion = ".rptdesign";
	protected static final String reportFolder = "temp/";

	protected static final String cacheExtencion = ".rptdocument";
	protected static final String cacheFolder = "temp/";
	
	
	private boolean cacheHasMaked = false;
	private String reportName;
	private long currentPage = 0;
	private long pagesCount = 1;

	private String reportHash;
	
	private Map<String, Object> parameters;
	
	
	public AbstractBirtPanel(String id,String reportName){
		this(id,reportName,new HashMap<String, Object>());
	}

	public AbstractBirtPanel(String id,String reportName,Map<String, Object> parameters){
		super(id);
		this.reportName = reportName;
		this.parameters = parameters;
		reportHash = makeReportHash();
		Component reportComponent = new Label(reportComponentName,""); 
		reportComponent.setEscapeModelStrings(false);
		add(reportComponent);
		IReportDocument cache = getReportCache();
		pagesCount = cache.getPageCount();
		cache.close();
	}
	
	private String getReportName() {
		return reportFolder+reportName+reportExtencion;
	}

	private String getReportPath() {
		return reportName;
	}
	
	private String makeReportHash() {
		return Md5Util.getMD5(reportName+Math.random());
	}
	
	public void setParameters(Map<String, Object> parameters) {
		if (!this.parameters.equals(parameters)){
			this.parameters = parameters;
			cacheHasMaked = false; 
		}
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	public void setParameter(String name, Object value){
		Object oldValue = parameters.get(name);
		if ((oldValue!=value)||(oldValue!=null && !oldValue.equals(value))){
			cacheHasMaked = false; 
		}
	}
	
	public Object getParameter(String name){
		return parameters.get(name);
	}
	
	
	private IReportDocument getReportCache(){
		IReportEngine engine = getReportEngine();
		if (!cacheHasMaked){
			try {
				IReportRunnable design;
				design = engine.openReportDesign(getReportName());
				 
				//Create task to run the report - use the task to execute the report and save to disk.
				IRunTask runTask = engine.createRunTask(design); 
				
				runTask.setParameterValues(parameters);
				runTask.run(getReportCachePath());		
				runTask.close();
				cacheHasMaked = true;
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		IReportDocument cache = null;
		try {
			cache = engine.openReportDocument(getReportCachePath());
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cache;
	}
	
	private String getReportCachePath() {
		OrientDbWebSession session = OrientDbWebSession.get();
		String path = cacheFolder+session.getUsername()+"/"+session.getId()+"/"+reportHash+"/"+reportName+cacheExtencion;
		return path;
	}
	
	private IReportEngine getReportEngine(){
		Module module = (Module)OrienteerWebApplication.get().getModuleByName("orienteer-birt");
		return module.engine;
	}
	
	private void updateReportOut() throws EngineException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		IReportDocument cache = getReportCache();
		IRenderTask renderTask = getReportEngine().createRenderTask(cache);
		

		IRenderOption options = makeRenderOption();
		renderTask.setRenderOption(options);
		//renderTask.setPageRange("1-5");
		renderTask.setPageNumber(currentPage+1);
		
		options.setOutputStream(buf);
		//run the report
		renderTask.render();
		cache.close();
		
		Object out;
		try {
			out = buf.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			out = buf.toString();
		}
		get(reportComponentName).setDefaultModelObject(out);

	}
	
	@Override
	protected void onBeforeRender() {
		try {
			updateReportOut();
		} catch (EngineException e) {
			e.printStackTrace();
		}
		super.onBeforeRender();
	}
	
	abstract protected IRenderOption makeRenderOption();

	//IPageable
	@Override
	public long getCurrentPage() {
		return currentPage;
	}

	@Override
	public void setCurrentPage(long page) {
		currentPage = page;
	}

	@Override
	public long getPageCount() {
		return pagesCount;
	}
	
}
