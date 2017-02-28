package org.orienteer.birt.component;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

	protected static final String cacheExtencion = ".rptdocument";
	protected static final String cacheFolder = "temp/";
	
	private long currentPage = 0;
	private long pagesCount = 1;

	private String reportHash;
	
	private Map<String, Object> parameters;
	
	
	public AbstractBirtPanel(String id,String reportFileName) throws EngineException, FileNotFoundException{
		this(id,reportFileName,new HashMap<String, Object>());
	}

	public AbstractBirtPanel(String id,String reportFileName,Map<String, Object> parameters) throws EngineException, FileNotFoundException{
		super(id);
		

		FileInputStream reportInputStream = new FileInputStream(reportFileName);

		this.parameters = parameters;
		init(reportInputStream);
	}

	public AbstractBirtPanel(String id,InputStream report,Map<String, Object> parameters) throws EngineException{
		super(id);
		this.parameters = parameters;
		init(report);
	}
	
	private void init(InputStream report) throws EngineException {
		reportHash = makeReportHash();
		Component reportComponent = new Label(reportComponentName,""); 
		reportComponent.setEscapeModelStrings(false);
		add(reportComponent);
		IReportDocument cache = getReportCache(report);
		pagesCount = cache.getPageCount();
		cache.close();
	}
	
	private String makeReportHash() {
		return Md5Util.getMD5(""+Math.random());
	}
	
	public Object getParameter(String name){
		return parameters.get(name);
	}
	
	
	private IReportDocument getReportCache(InputStream reportInputStream) throws EngineException{
		IReportEngine engine = getReportEngine();
		IReportRunnable design;
		design = engine.openReportDesign(reportInputStream);
		 
		//Create task to run the report - use the task to execute the report and save to disk.
		IRunTask runTask = engine.createRunTask(design); 
		
		runTask.setParameterValues(parameters);
		runTask.run(getReportCachePath());		
		runTask.close();
		IReportDocument cache = engine.openReportDocument(getReportCachePath());
		return cache;
	}
	
	private IReportDocument getReportCache() throws EngineException{
		return getReportEngine().openReportDocument(getReportCachePath());
	}
	
	private String getReportCachePath() {
		OrientDbWebSession session = OrientDbWebSession.get();
		String path = cacheFolder+session.getUsername()+"/"+session.getId()+"/"+reportHash+cacheExtencion;
		return path;
	}
	
	private IReportEngine getReportEngine(){
		Module module = (Module)OrienteerWebApplication.get().getModuleByName("orienteer-birt");
		return module.getEngine();
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
