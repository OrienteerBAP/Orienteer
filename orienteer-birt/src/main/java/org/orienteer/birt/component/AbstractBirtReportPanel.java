package org.orienteer.birt.component;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

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
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.OdaDataSourceHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.orienteer.birt.Module;
import org.orienteer.birt.orientdb.impl.Connection;
import org.orienteer.birt.orientdb.impl.Driver;
import org.orienteer.core.OrienteerWebApplication;

import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
//import java.io.

/**
 *	Base panel for other BIRT reports panels
 */
public abstract class AbstractBirtReportPanel extends Panel implements IPageable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String REPORT_COMPONENT_NAME = "report";

	protected static final String CACHE_EXTENCION = ".rptdocument";
	protected static final String CACHE_FOLDER = System.getProperty("java.io.tmpdir");//"temp/";
	
	private long currentPage = 0;
	private long pagesCount = 1;

	private String reportHash;
	
	private Map<String, Object> parameters;
	private boolean useLocalDB = false;
	
	public AbstractBirtReportPanel(String id,String reportFileName,Map<String, Object> parameters,boolean useLocalDB) throws EngineException, FileNotFoundException{
		super(id);
		

		FileInputStream reportInputStream = new FileInputStream(reportFileName);

		this.parameters = parameters;
		this.useLocalDB = useLocalDB;
		init(reportInputStream);
	}

	public AbstractBirtReportPanel(String id,InputStream report,Map<String, Object> parameters,boolean useLocalDB) throws EngineException{
		super(id);
		this.parameters = parameters;
		this.useLocalDB = useLocalDB;
		init(report);
	}
	
	private void init(InputStream report) throws EngineException {
		reportHash = makeReportHash();
		Component reportComponent = new Label(REPORT_COMPONENT_NAME,""); 
		reportComponent.setEscapeModelStrings(false);
		reportComponent.setOutputMarkupId(true);
		add(reportComponent);
		IReportDocument cache = getReportCache(report);
		pagesCount = cache.getPageCount();
		cache.close();
	}
	
	public Component getReportComponent() {
		return get(REPORT_COMPONENT_NAME);
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
		if (isUseLocalDB()){
			updateDBUriToLocal(design);
		}
		//////////////////////////////////////////////
		 
		//design.getDesignInstance().getDataSource("").set
		//getting available report parameters
		//IGetParameterDefinitionTask paramTask = engine.createGetParameterDefinitionTask(design);
		//paramTask.getParameterDefn("my_parameter_name").getHandle().getElement().getProperty(null, "dataType").toString();
		//paramTask.pa
		
		//Create task to run the report - use the task to execute the report and save to disk.
		IRunTask runTask = engine.createRunTask(design);

		runTask.setParameterValues(parameters);
		//HashMap parameters1 = runTask.getParameterValues();
		runTask.run(getReportCachePath());		
		runTask.close();
		IReportDocument cache = engine.openReportDocument(getReportCachePath());
		return cache;
	}
	
	private static void updateDBUriToLocal(IReportRunnable design){
		ReportDesignHandle handle = (ReportDesignHandle) design.getDesignHandle();
		SlotHandle datasources = handle.getDataSources();
		Iterator<?> dsiterator = datasources.iterator();
		for (;dsiterator.hasNext();) {
			DesignElementHandle dsHandle = (DesignElementHandle) dsiterator.next();
			if (dsHandle instanceof OdaDataSourceHandle ) {
				OdaDataSourceHandle odash = (OdaDataSourceHandle)dsHandle;
				if (odash.getExtensionID().equals(Driver.ODA_DATA_SOURCE_ID)){
					try {
						odash.setProperty(Connection.DB_URI_PROPERTY, OrientDbWebApplication.get().getOrientDbSettings().getDBUrl());
					} catch (SemanticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}		
	}
	
	
	public IReportDocument getReportCache() throws EngineException{
		return getReportEngine().openReportDocument(getReportCachePath());
	}
	
	public String getReportCachePath() {
		OrientDbWebSession session = OrientDbWebSession.get();
		String path = CACHE_FOLDER+session.getUsername()+"/"+session.getId()+"/"+reportHash+CACHE_EXTENCION;
		return path;
	}
	
	public IReportEngine getReportEngine(){
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
		get(REPORT_COMPONENT_NAME).setDefaultModelObject(out);

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

	public boolean isUseLocalDB() {
		return useLocalDB;
	}
}
