package org.orienteer.camel.widget;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.camel.component.OrientDBComponent;
import org.orienteer.camel.tasks.CamelEventHandler;
import org.orienteer.camel.tasks.OCamelTaskSession;
import org.orienteer.camel.tasks.OCamelTaskSessionCallback;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OEntityColumn;
import org.orienteer.core.component.table.OPropertyValueColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.tasks.ITaskSession;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
/**
 * Widget for Orienteer Camel integration, linked to OIntegrationConfig
 *
 */

@Widget(domain="document",selector="OIntegrationConfig", id=CamelWidget.WIDGET_TYPE_ID, order=20, autoEnable=true)
public class CamelWidget extends AbstractWidget<ODocument>{

	public static final String WIDGET_TYPE_ID = "camelIntegration";
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(CamelWidget.class);
	
	public static final MetaDataKey<Map<String,CamelContext>> INTEGRATION_SESSIONS_KEY = new MetaDataKey<Map<String,CamelContext>>()
	{
		private static final long serialVersionUID = 1L;
	};
	
	private Form form;
	
	private static final List<String> COLUMNS_NAMES = new ArrayList<String>();
	static{
		COLUMNS_NAMES.add(ITaskSession.Field.THREAD_NAME.fieldName());
		COLUMNS_NAMES.add(ITaskSession.Field.STATUS.fieldName());
		COLUMNS_NAMES.add(ITaskSession.Field.START_TIMESTAMP.fieldName());
		COLUMNS_NAMES.add(ITaskSession.Field.FINISH_TIMESTAMP.fieldName());
		COLUMNS_NAMES.add(ITaskSession.Field.PROGRESS_CURRENT.fieldName());
		COLUMNS_NAMES.add(ITaskSession.Field.PROGRESS_FINAL.fieldName());
	}
	
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	private class CamelContextModel extends LoadableDetachableModel<CamelContext>{
		private static final long serialVersionUID = 1L;
		@Override
		protected CamelContext load() {
			return getOrMakeContext();
		}
	}
	
	
	public CamelWidget(String id, IModel<ODocument> model, final IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);

		form = new Form<Void>("form");
        
		IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
		
		OClass taskSessionClass = getModelObject().getDatabase().getMetadata().getSchema().getClass(OCamelTaskSession.TASK_SESSION_CLASS);
		
		List<IColumn<ODocument, String>> columns = makeColumns(taskSessionClass);
		
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>(
				"select from "+OCamelTaskSession.TASK_SESSION_CLASS+" where "+
				OCamelTaskSession.Field.CONFIG.fieldName()+"="+CamelWidget.this.getModelObject().getIdentity());
		oClassIntrospector.defineDefaultSorting(provider, taskSessionClass);
		OrienteerDataTable<ODocument, String> table = 
				new OrienteerDataTable<ODocument, String>("table", columns, provider, 20);
		
		form.add(table);
		table.addCommand(makeStartButton());
		table.addCommand(makeStopButton());
		table.addCommand(makeSuspendButton());
		form.setOutputMarkupId(true);

		add(form);
	}
	
	private List<IColumn<ODocument, String>> makeColumns(OClass taskSessionClass) {
		ArrayList<IColumn<ODocument, String>> columns = new ArrayList<IColumn<ODocument,String>>();
		boolean makeLink = true;
		for (String string : COLUMNS_NAMES) {
			if (makeLink){
				columns.add(new OEntityColumn(taskSessionClass.getProperty(string),true, DisplayMode.VIEW.asModel()));
				makeLink = false;
			}else{
				columns.add(new OPropertyValueColumn(string,taskSessionClass.getProperty(string), DisplayMode.VIEW.asModel()));		
			}
		}
		return columns;
	}
	
	private Command makeStartButton() {
		return new AjaxCommand("start","integration.start") {
			@Override
			protected void onInitialize() {
				super.onInitialize();
				setIcon(FAIconType.play);
				setBootstrapType(BootstrapType.SUCCESS);
				setChangingDisplayMode(true);
			}
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					
					ODocument doc = (ODocument) CamelWidget.this.getDefaultModelObject();
					CamelContext context = getOrMakeContextByRid(doc.getIdentity().toString());

					if (context.getStatus().isSuspended()){
						context.resume();
						target.add(CamelWidget.this.form);
					}else if (!context.getStatus().isStarted()){
						clearContext(context);
						String script = doc.field("script");
						
						RoutesDefinition routes = context.loadRoutesDefinition(new ByteArrayInputStream( script.getBytes()));
						context.addRouteDefinitions(routes.getRoutes());

						context.start();
						target.add(CamelWidget.this.form);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public boolean isEnabled() {
				CamelContext context = getOrMakeContext();
				ServiceStatus status = context.getStatus();
				if (status.isStarted()){
					return false;
				}
				return super.isEnabled();
			}
		};

	}

	
	private Command makeSuspendButton() {
		return new AjaxCommand("suspend","integration.suspend") {
			@Override
			protected void onInitialize() {
				super.onInitialize();
				setIcon(FAIconType.pause);
				setBootstrapType(BootstrapType.WARNING);
				setChangingDisplayMode(true);
			}
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					CamelContext context = getOrMakeContext();
					ServiceStatus status = context.getStatus();
					if (status.isSuspended()){
						context.start();
					}else if(status.isStarted()){
						context.suspend();
					}
					target.add(CamelWidget.this.form);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public boolean isEnabled() {
				CamelContext context = getOrMakeContext();
				ServiceStatus status = context.getStatus();
				if (!status.isStarted()){
					return false;
				}
				return super.isEnabled();
			}
		};
	}
	

	private Command makeStopButton() {
		return new AjaxCommand("stop","integration.stop") {
			@Override
			protected void onInitialize() {
				super.onInitialize();
				setIcon(FAIconType.stop);
				setBootstrapType(BootstrapType.DANGER);
				setChangingDisplayMode(true);
			}
			@Override
			public void onClick(AjaxRequestTarget target) {
				CamelContext context = getOrMakeContext();
				try {
					context.stop();
					target.add(CamelWidget.this.form);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public boolean isEnabled() {
				CamelContext context = getOrMakeContext();
				ServiceStatus status = context.getStatus();
				if (status.isStopped()){
					return false;
				}
				return super.isEnabled();
			}
		};
	}
	
	private CamelContext getOrMakeContext(){
		ODocument doc = (ODocument) CamelWidget.this.getDefaultModelObject();
		return getOrMakeContextByRid(doc.getIdentity().toString());
	}

	private CamelContext getOrMakeContextByRid(String rid){
		CamelContext context;
		Map<String,CamelContext> contextMap = getApplication().getMetaData(CamelWidget.INTEGRATION_SESSIONS_KEY);
		if (contextMap.containsKey(rid)){
			context = contextMap.get(rid);
		}else{
			IOrientDbSettings dbSettings = OrientDbWebApplication.get().getOrientDbSettings();
			OrientDbWebSession session = OrientDbWebSession.get();
			if (session.getUsername()==null){
				throw new UnauthorizedActionException(this, Component.RENDER);
			}
			context = new DefaultCamelContext();
			Map<String, String> properties = context.getProperties();
			properties.put(OrientDBComponent.DB_URL, dbSettings.getDBUrl());
			properties.put(OrientDBComponent.DB_USERNAME, session.getUsername());
			properties.put(OrientDBComponent.DB_PASSWORD, session.getPassword());
			context.setProperties(properties);
			
			context.getManagementStrategy().addEventNotifier(new CamelEventHandler(new OCamelTaskSessionCallback(context),rid,context));

			contextMap.put(rid, context);
		}
		return context;
	}
	
	private void clearContext(CamelContext context) throws Exception{
		List<RouteDefinition> definitions = context.getRouteDefinitions();
		if (!definitions.isEmpty()){
			context.removeRouteDefinitions(new ArrayList<RouteDefinition>(definitions));
		}
	}
	
	
	@Override
	protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("integration.camel");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
