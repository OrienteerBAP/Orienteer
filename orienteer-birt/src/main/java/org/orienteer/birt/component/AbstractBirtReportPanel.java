package org.orienteer.birt.component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.eclipse.birt.data.engine.executor.cache.Md5Util;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IHTMLImageHandler;
import org.eclipse.birt.report.engine.api.IImage;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
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
import org.orienteer.birt.AbstractBirtHTMLImageHandler;
import org.orienteer.birt.BirtImage;
import org.orienteer.birt.Module;
import org.orienteer.birt.component.service.BirtReportParameterDefinition;
import org.orienteer.birt.component.service.IBirtReportConfig;
import org.orienteer.birt.orientdb.impl.Connection;
import org.orienteer.birt.orientdb.impl.Driver;
import org.orienteer.core.OrienteerWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
//import java.io.

/**
 *	Base panel for other BIRT reports panels
 */
public abstract class AbstractBirtReportPanel extends Panel implements IPageable, IBirtReportData, IResourceListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AbstractBirtHTMLImageHandler.class);
	protected static final String REPORT_COMPONENT_NAME = "reportContent";
	protected static final String RESOURCE_IMAGE_ID = "imageId";

	protected static final String CACHE_EXTENCION = ".rptdocument";
	protected static final String CACHE_FOLDER = System.getProperty("java.io.tmpdir")+"/birt_cache";//"temp/";
	
	private long currentPage = 0;
	private long pagesCount = 1;
	private IBirtReportConfig config;

	private String reportHash;
	private List<BirtReportParameterDefinition> paramDefinitions;
	private List<BirtReportParameterDefinition> hiddenParamDefinitions;
	
	private AbstractBirtHTMLImageHandler imageHandler = new AbstractBirtHTMLImageHandler() {
		
		@Override
		protected String urlFor(String id, BirtImage image) {
			return AbstractBirtReportPanel.this.urlFor(IResourceListener.INTERFACE, new PageParameters().add(RESOURCE_IMAGE_ID, id)).toString();
		}
	};
	
	public AbstractBirtReportPanel(String id,IBirtReportConfig config) throws EngineException{
		super(id);
		this.config = config;
		paramDefinitions = new ArrayList<BirtReportParameterDefinition>();
		hiddenParamDefinitions = new ArrayList<BirtReportParameterDefinition>();
		reportHash = makeReportHash();
		updateReportCache();
		
		Component reportComponent = new Label(REPORT_COMPONENT_NAME,""); 
		reportComponent.setEscapeModelStrings(false);
		reportComponent.setOutputMarkupId(true);
		add(reportComponent);
		
	}
	
	
	
	@Override
	public void onResourceRequested() {
		RequestCycle requestCycle = RequestCycle.get();
		IRequestParameters params = requestCycle.getRequest().getRequestParameters();
		String imageId = params.getParameterValue(RESOURCE_IMAGE_ID).toOptionalString();
		if(imageId!=null) {
			IResource resource = imageHandler.getBirtImageAsResource(imageId);
			if(resource!=null) {
				resource.respond(new Attributes(requestCycle.getRequest(), requestCycle.getResponse(), null));
			}
		}
	}
	
	@Override
	public IHTMLImageHandler getIHTMLImageHandler() {
		return imageHandler;
	}

	@Override
	public String getOutName(){
		return config.getOutName();
	}
	
	public Component getReportComponent() {
		return get(REPORT_COMPONENT_NAME);
	}
	
	private String makeReportHash() {
		return Md5Util.getMD5(""+Math.random());
	}
	
	public Object getParameter(String name){
		return config.getParameters().get(name);
	}
	
	public Object setParameter(String name,Object value){
		return config.getParameters().put(name, value);
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
		//paramTask = engine.createGetParameterDefinitionTask(design);
		updateParametersDefinitions(design);
		//paramTask.getParameterDefn("my_parameter_name").getHandle().getElement().getProperty(null, "dataType").toString();
		//paramTask.pa
		
		//Create task to run the report - use the task to execute the report and save to disk.
		IRunTask runTask = engine.createRunTask(design);

		runTask.setParameterValues(config.getParameters());
		//HashMap parameters1 = runTask.getParameterValues();
		runTask.run(getReportCachePath());		
		runTask.close();
		IReportDocument cache = engine.openReportDocument(getReportCachePath());
		return cache;
	}
	
	public void updateReportCache() throws EngineException{
		IReportDocument cache = getReportCache(getConfig().getReportDataStream());
		pagesCount = cache.getPageCount();
		cache.close();		
	}
	
	
	public List<BirtReportParameterDefinition> getParametersDefenitions() {
		return paramDefinitions;
	}
	
	public List<BirtReportParameterDefinition> getHiddenParametersDefinitions() {
		return hiddenParamDefinitions;
	}
	
	
	@SuppressWarnings("rawtypes")
	private static void updateDBUriToLocal(IReportRunnable design){
		ReportDesignHandle handle = (ReportDesignHandle) design.getDesignHandle();
		SlotHandle datasources = handle.getDataSources();
		Iterator dsiterator = datasources.iterator();
		for (;dsiterator.hasNext();) {
			DesignElementHandle dsHandle = (DesignElementHandle) dsiterator.next();
			if (dsHandle instanceof OdaDataSourceHandle ) {
				OdaDataSourceHandle odash = (OdaDataSourceHandle)dsHandle;
				if (odash.getExtensionID().equals(Driver.ODA_DATA_SOURCE_ID)){
					try {
						odash.setProperty(Connection.DB_URI_PROPERTY, OrientDbWebApplication.get().getOrientDbSettings().getDBUrl());
						odash.setProperty(Connection.DB_USER_PROPERTY, OrientDbWebSession.get().getUsername());
						odash.setProperty(Connection.DB_PASSWORD_PROPERTY, OrientDbWebSession.get().getPassword());
					} catch (SemanticException e) {
						LOG.error("Cen't part BIRT xml file", e);
					}
				}
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	private void updateParametersDefinitions(IReportRunnable design) {
		IGetParameterDefinitionTask paramTask = getReportEngine().createGetParameterDefinitionTask(design);
		
		Set<Object> visibleParams = getConfig().getVisibleParameters();
		if (visibleParams!=null){
			for (Object object : visibleParams) {
				IParameterDefnBase iParameterDefnBase = paramTask.getParameterDefn((String) object);
				paramDefinitions.add(new BirtReportParameterDefinition(iParameterDefnBase));
			}
		}
		
		Collection<IParameterDefnBase> defs = paramTask.getParameterDefns(false);
		for (Iterator<?> iterator = defs.iterator(); iterator.hasNext();) {
			IParameterDefnBase iParameterDefnBase = (IParameterDefnBase) iterator.next();
			if (visibleParams==null || !visibleParams.contains(iParameterDefnBase.getName())){
				hiddenParamDefinitions.add(new BirtReportParameterDefinition(iParameterDefnBase));
			}
		}
	}
	
	
	public IReportDocument getReportCache() throws EngineException{
		return getReportEngine().openReportDocument(getReportCachePath());
	}
	
	public String getReportCachePath() {
		OrientDbWebSession session = OrientDbWebSession.get();
		String path = CACHE_FOLDER+"/"+session.getUsername()+"/"+session.getId()+"/"+reportHash+CACHE_EXTENCION;
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
		options.setOutputStream(buf);
		options.setImageHandler(imageHandler);
		
		renderTask.setRenderOption(options);
		//renderTask.setPageRange("1-5");
		renderTask.setPageNumber(currentPage+1);
		
		//run the report
		renderTask.render();
		cache.close();
		
		Object out;
		try {
			out = buf.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("Encoding is not supported", e);
			out = buf.toString();
		}
		get(REPORT_COMPONENT_NAME).setDefaultModelObject(out);

	}
	
	@Override
	protected void onBeforeRender() {
		try {
			updateReportOut();
		} catch (EngineException e) {
			LOG.error("Can't update report output", e);
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
		return config.isUseLocalDB();
	}
	
	public IBirtReportConfig getConfig() {
		return config;
	}
}
