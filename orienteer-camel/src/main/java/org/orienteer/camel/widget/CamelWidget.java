package org.orienteer.camel.widget;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
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
	
	private static final Logger LOG = LoggerFactory.getLogger(CamelWidget.class);
//    private FileUploadField fileUpload;
	//private CamelContext context;

	public CamelWidget(String id, IModel<Void> model, final IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
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
        Form<?> form = new Form<Void>("form");/*{
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
        form.add(makeStartButton());
        form.add(makeStopButton());
        add(form);
	}
	
	protected Component makeStartButton() {
		return new AjaxLink("start") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					CamelContext context = new DefaultCamelContext();
					ODocument doc = (ODocument) CamelWidget.this.getDefaultModelObject();
					String script = doc.field("script");
					RoutesDefinition routes = context.loadRoutesDefinition(new ByteArrayInputStream( script.getBytes()));
					context.addRouteDefinitions(routes.getRoutes());
					context.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	protected Component makeStopButton() {
		return new AjaxLink("stop") {
			@Override
			public void onClick(AjaxRequestTarget target) {
			}
		};
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
