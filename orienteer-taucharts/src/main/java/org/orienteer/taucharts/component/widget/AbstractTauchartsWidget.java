package org.orienteer.taucharts.component.widget;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.taucharts.component.TauchartsPanel;

import com.orientechnologies.orient.core.record.impl.ODocument;


/**
 * 
 * Widget for display {@link TauchartsPanel}
 *
 * @param <T> input data type
 */

public abstract class AbstractTauchartsWidget<T> extends AbstractWidget<T> {
	private static final long serialVersionUID = 1L;
	public static final String WIDGET_OCLASS_NAME="AbstractTauchartsWidget";

	public static final String TYPE_PROPERTY_NAME = "chartType";
	public static final String X_PROPERTY_NAME = "chartX";
	public static final String X_LABEL_PROPERTY_NAME = "chartXLabel";
	public static final String Y_PROPERTY_NAME = "chartY";
	public static final String Y_LABEL_PROPERTY_NAME = "chartYLabel";
	public static final String COLOR_PROPERTY_NAME = "chartColorBy";
	public static final String PLUGINS_PROPERTY_NAME = "chartPlugins";
	public static final String QUERY_PROPERTY_NAME = "chartQuery";
	
	public static final String TYPE_OCLASS = "TauchartsType";
	
	@SuppressWarnings("unchecked")
	public AbstractTauchartsWidget(String id, IModel<T> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		if (getWidgetDocument().field(QUERY_PROPERTY_NAME)!=null && getWidgetDocument().field(TYPE_PROPERTY_NAME)!=null){
			makeChartPanel();
		}else{
			add(new Label("tauchart","Configure widget first"));
		}
	}

	protected abstract void makeChartPanel();
	
	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.line_chart);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.taucharts","Chart");
	}


}
