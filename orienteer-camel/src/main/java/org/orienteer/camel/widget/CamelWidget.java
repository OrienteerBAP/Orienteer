package org.orienteer.camel.widget;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.StyleAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.lang.Bytes;
import org.orienteer.camel.component.CamelEventHandler;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.UnregistredPropertyEditPanel;
import org.orienteer.core.web.schema.OPropertyPage;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.impl.ODocument;

@Widget(domain="document",selector="OIntegrationSession", id=CamelWidget.WIDGET_TYPE_ID, order=20, autoEnable=true)
public class CamelWidget extends AbstractWidget<Void>{

	public static final String WIDGET_TYPE_ID = "camelIntegration";
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(CamelWidget.class);
	
	public static final MetaDataKey<Map<String,CamelContext>> INTEGRATION_SESSIONS_KEY = new MetaDataKey<Map<String,CamelContext>>()
	{
		private static final long serialVersionUID = 1L;
	};
	
//    private FileUploadField fileUpload;
	//private CamelContext context;
	private Form form;
	private ClassAttributeModifier disabled;

	public CamelWidget(String id, IModel<Void> model, final IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		disabled = new ClassAttributeModifier() {
			private static final long serialVersionUID = 1L;
			@Override
			protected Set<String> update(Set<String> oldClasses) {
				oldClasses.add("disabled");
				return oldClasses;
			}
			@Override
			public boolean isTemporary(Component component) {
				return true;
			}
		};
		/*
		CamelContext context = new DefaultCamelContext();
		try {
			ODocument doc = (ODocument) CamelWidget.this.getDefaultModelObject();
			String script = doc.field("script");
			RoutesDefinition routes = context.loadRoutesDefinition(new ByteArrayInputStream( script.getBytes()));
			context.addRouteDefinitions(routes.getRoutes());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
        form = new Form<Void>("form");/*{
        	@Override
        	protected void onSubmit() {
   				CamelContext context = new DefaultCamelContext();
   				try {
   					ODocument doc = (ODocument) CamelWidget.this.getDefaultModelObject();
   					String script = doc.field("script");
   					LOG.info(script);
   					LOG.info((String) doc.field("name"));
   				//context.getManagementStrategy().addEventNotifier(new CamelEventHandler(olog.getIdentity().toString()));
   				
   					RoutesDefinition routes = context.loadRoutesDefinition(new ByteArrayInputStream( script.getBytes()));
   					context.addRouteDefinitions(routes.getRoutes());
					context.start();
					Thread.sleep(3000);
					context.stop();
	    		
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		super.onSubmit();
        	}
        };*/
        //form.setMultiPart(true);
        //form.setMaxSize(Bytes.kilobytes(100));
    	//form.add(fileUpload = new FileUploadField("fileUpload"));
        
        
        form.add(makeStatusComponent());
        form.add(makeStartButton());
        form.add(makeStopButton());
        form.add(makePauseButton());
        form.setOutputMarkupId(true);

        add(form);
	}
	
	private Component makeStatusComponent(){
		Component result = new Label("status"){
			@Override
			protected void onBeforeRender() {
				CamelContext context = getOrMakeContext();
				ServiceStatus status = context.getStatus();
				if (status.isStopped()){
					setDefaultModelObject("Session has stopped");
				}else if(status.isStarted()){
					setDefaultModelObject("Session is running");
				}else if(status.isSuspended()){
					setDefaultModelObject("Session is suspended");
				}else{
					setDefaultModelObject("Status changing...");
				}
				super.onBeforeRender();
			}
		};
	
		return result;
	}
	
	private Component makeStartButton() {
		return new AjaxLink("start") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					ODocument doc = (ODocument) CamelWidget.this.getDefaultModelObject();
					CamelContext context = getOrMakeContextByRid(doc.getIdentity().toString());
					if (context.getRoutes().isEmpty()){
						String script = doc.field("script");
						RoutesDefinition routes = context.loadRoutesDefinition(new ByteArrayInputStream( script.getBytes()));
						context.addRouteDefinitions(routes.getRoutes());
					}
					if (!context.getStatus().isStarted()){
						context.start();
						target.add(CamelWidget.this.form);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			protected void onBeforeRender() {
				
				CamelContext context = getOrMakeContext();
				ServiceStatus status = context.getStatus();
				if (status.isStarted()){
					this.add(disabled);
				}
				super.onBeforeRender();
			}
		};
	}
	
	private Component makePauseButton() {
		return new AjaxLink("pause") {
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
			protected void onBeforeRender() {
				CamelContext context = getOrMakeContext();
				ServiceStatus status = context.getStatus();
				if (!status.isStarted()){
					this.add(disabled);
				}
				super.onBeforeRender();
			}
		};
	}
	

	private Component makeStopButton() {
		return new AjaxLink("stop") {
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
			protected void onBeforeRender() {
				CamelContext context = getOrMakeContext();
				ServiceStatus status = context.getStatus();
				if (status.isStopped()){
					this.add(disabled);
				}
				super.onBeforeRender();
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
			contextMap.put(rid, context);
		}
		return context;
	}
	
	@Override
	protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("integration.camel");
	}

}
