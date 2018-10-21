package org.orienteer.taucharts.component.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.Widget;
import org.orienteer.taucharts.component.TauchartsConfig;
import org.orienteer.taucharts.component.TauchartsPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget displays {@link TauchartsPanel} on document list 
 */
@Widget(id="taucharts-browse", domain="browse", order=10, autoEnable=false,oClass=AbstractTauchartsWidget.WIDGET_OCLASS_NAME)
public class TauchartsBrowseWidget extends AbstractTauchartsWidget<OClass>{
	private static final long serialVersionUID = 1L;

	public TauchartsBrowseWidget(String id, IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
	@Override
	protected TauchartsPanel newChartPanel(String id){
		return new TauchartsPanel(id, new TauchartsConfig(getWidgetDocument()));		
	}

}
