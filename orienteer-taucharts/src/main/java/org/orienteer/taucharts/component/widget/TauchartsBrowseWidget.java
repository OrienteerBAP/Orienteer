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
	protected TauchartsPanel makeChartPanel(){
		List<String> plugins = new ArrayList<String>();
		Set<ODocument> pluginsLinks = getWidgetDocument().field(PLUGINS_PROPERTY_NAME);
		if (pluginsLinks!=null){
			for (ODocument oDocument : pluginsLinks) {
				plugins.add((String) oDocument.field("alias"));
			}
		}
		TauchartsPanel panel;
		add(panel = new TauchartsPanel(
				"tauchart",
				new TauchartsConfig(
					(String)(((ODocument) getWidgetDocument().field(TYPE_PROPERTY_NAME)).field("alias")),
					(Collection<String>)getWidgetDocument().field(X_PROPERTY_NAME),
					(Collection<String>)getWidgetDocument().field(Y_PROPERTY_NAME),
					(String)getWidgetDocument().field(COLOR_PROPERTY_NAME),
					plugins,
					(String)getWidgetDocument().field(QUERY_PROPERTY_NAME),
					(String) getWidgetDocument().field(X_LABEL_PROPERTY_NAME),
					(String) getWidgetDocument().field(Y_LABEL_PROPERTY_NAME),
					(Boolean) getWidgetDocument().field(USING_REST_PROPERTY_NAME),
					(String) getWidgetDocument().field(CONFIG_PROPERTY_NAME)
				)
		));		
		return panel;
	}

}
