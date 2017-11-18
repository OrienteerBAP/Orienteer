package org.orienteer.birt.component.widget;

import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.eclipse.birt.report.engine.api.EngineException;
import org.orienteer.birt.component.BirtManagedHtmlReportPanel;
import org.orienteer.birt.component.service.BirtReportODocumentConfig;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Base widget for BIRT report widgets with ajax report loading
 * @param <T> Report object type
 */
public class AbstractBirtWidget<T> extends AbstractWidget<T>{

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AbstractBirtWidget.class);
	public static final String OCLASS_NAME = "BirtWidget";
	public static final String PARAMETERS_FIELD_NAME = "parameters";
	public static final String REPORT_FIELD_NAME = "report";
	public static final String USE_LOCAL_BASE_FIELD_NAME = "useLocalDB";
	public static final String VISIBLE_PARAMETERS_FIELD_NAME = "visibleParameters";


	
	public AbstractBirtWidget(String id, IModel<T> model, IModel<ODocument> widgetDocumentModel,final Map<String,Object> additionalParameters) {
		super(id, model, widgetDocumentModel);
		/*
		 * parameters
		 * report byte[]
		 * */
		
		AjaxLazyLoadPanel panel = new AjaxLazyLoadPanel("reportHolder")
		{
		  /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		@Override
		  public Component getLazyLoadComponent(String id)
		  {
		    try {
				return new BirtManagedHtmlReportPanel(id,new BirtReportODocumentConfig(AbstractBirtWidget.this.getWidgetDocumentModel(),additionalParameters));
			} catch (EngineException e) {
				String message = e.getMessage();
				if (!Strings.isEmpty(message)){
					error(message);
				}
				LOG.error("BIRT HTML report panel can't be added", e);
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
