package org.orienteer.taucharts.component.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.Widget;
import org.orienteer.taucharts.component.TauchartsConfig;
import org.orienteer.taucharts.component.TauchartsPanel;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget displays {@link TauchartsPanel} on document page
 * All document properties can be getting in SQL query as ":property_name" 
 */
@Widget(id="taucharts-document", domain="document", oClass=AbstractTauchartsWidget.WIDGET_OCLASS_NAME, order=10, autoEnable=false)
public class TauchartsDocumentWidget extends AbstractTauchartsWidget<ODocument>{
	private static final long serialVersionUID = 1L;

	public TauchartsDocumentWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
	@Override
	protected TauchartsPanel newChartPanel(String id){
		return new TauchartsPanel("tauchart", getModel(), new TauchartsConfig(getWidgetDocument()));
	}
}
