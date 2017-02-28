package org.orienteer.birt.component.widget;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.eclipse.birt.report.engine.api.EngineException;
import org.orienteer.birt.component.BirtPaginatedHtmlPanel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.sun.mail.handlers.message_rfc822;

public class AbstractBirtWidget<T> extends AbstractWidget<T>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String OCLASS_NAME = "BirtWidget";
	public static final String PARAMETERS_FIELD_NAME = "parameters";
	public static final String REPORT_FIELD_NAME = "report";


	
	public AbstractBirtWidget(String id, IModel<T> model, IModel<ODocument> widgetDocumentModel,final Map<String,Object> additionalParameters) {
		super(id, model, widgetDocumentModel);
		/*
		 * parameters
		 * report byte[]
		 * */
		
		AjaxLazyLoadPanel panel = new AjaxLazyLoadPanel("report")
		{
		  /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		@Override
		  public Component getLazyLoadComponent(String id)
		  {
		    try {
		    	ODocument modelObject = AbstractBirtWidget.this.getWidgetDocumentModel().getObject();
				byte[] reportData = modelObject.field(REPORT_FIELD_NAME);
		    	if (reportData==null || reportData.length==0){
		    		throw new Exception("Configure report first");
		    	}
				InputStream reportStream = new ByteArrayInputStream(reportData);
				Map<String,Object> parameters = modelObject.field(PARAMETERS_FIELD_NAME);
				if (additionalParameters!=null){
					parameters.putAll(additionalParameters);
				}
				
				return new BirtPaginatedHtmlPanel(id,reportStream,parameters);
			} catch (Exception e) {
				//error(e.getMessage());
				String message = e.getMessage();
				e.printStackTrace();
				return new Label(id,"Report error: "+message);
			}
		  }
		};
		add(panel);	
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.table);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.birt");
	}
}
