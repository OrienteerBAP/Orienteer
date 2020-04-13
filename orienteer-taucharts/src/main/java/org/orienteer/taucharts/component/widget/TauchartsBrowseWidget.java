package org.orienteer.taucharts.component.widget;

import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.Widget;
import org.orienteer.taucharts.component.TauchartsConfig;
import org.orienteer.taucharts.component.AbstractTauchartsPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget displays {@link AbstractTauchartsPanel} on document list 
 */
@Widget(id="taucharts-browse", domain="browse", order=10, autoEnable=false,oClass=AbstractTauchartsWidget.WIDGET_OCLASS_NAME)
public class TauchartsBrowseWidget extends AbstractTauchartsWidget<OClass>{
	private static final long serialVersionUID = 1L;

	public TauchartsBrowseWidget(String id, IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
	@Override
	protected boolean isConfigValid() {
		return getWidgetDocument().field(TYPE_PROPERTY_NAME)!=null;
	}
	
	@Override
	protected AbstractTauchartsPanel newChartPanel(String id){
		return new AbstractTauchartsPanel(id, new TauchartsConfig(getWidgetDocument())) {

			@Override
			protected String getDefaultSql() {
				return "select from "+TauchartsBrowseWidget.this.getModelObject().getName();
			}
			
		};		
	}

}
