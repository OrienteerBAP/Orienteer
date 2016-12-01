package org.orienteer.camel.widget;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.model.ContextScanDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.camel.component.CamelEventHandler;
import org.orienteer.camel.component.OrientDBComponent;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

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
	
	public static final List<String> CONTEXT_DATA_LIST = new ArrayList<String>();
	static
	{
		CONTEXT_DATA_LIST.add("name");
		CONTEXT_DATA_LIST.add("status");
		CONTEXT_DATA_LIST.add("uptime");
		CONTEXT_DATA_LIST.add("version");
	}	
	
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
        
        OrienteerStructureTable<CamelContext, String> structuredTable = new OrienteerStructureTable<CamelContext, String>("table", new CamelContextModel(), CONTEXT_DATA_LIST) {
			private static final long serialVersionUID = 1L;
			@Override
			protected Component getValueComponent(String id, IModel<String> rowModel) {
				return new Label(id,new PropertyModel<>(getModel(), rowModel.getObject()));
			}
			@Override
			protected IModel<?> getLabelModel(Component resolvedComponent, IModel<String> rowModel) {
				return new SimpleNamingModel<String>("integration."+rowModel.getObject());
			}
		};
			
		
		form.add(structuredTable);
		structuredTable.addCommand(makeStartButton());
		structuredTable.addCommand(makeStopButton());
		structuredTable.addCommand(makeSuspendButton());
		form.setOutputMarkupId(true);

		add(form);
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
			context = new DefaultCamelContext();
			context.getManagementStrategy().addEventNotifier(new CamelEventHandler(""));

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
