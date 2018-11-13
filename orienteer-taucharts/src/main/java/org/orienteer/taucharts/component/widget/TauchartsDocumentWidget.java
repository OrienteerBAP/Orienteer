package org.orienteer.taucharts.component.widget;

import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.Widget;
import org.orienteer.taucharts.component.TauchartsConfig;
import org.orienteer.taucharts.component.AbstractTauchartsPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget displays {@link AbstractTauchartsPanel} on document page
 * All document properties can be getting in SQL query as ":property_name" 
 */
@Widget(id="taucharts-document", domain="document", oClass=AbstractTauchartsWidget.WIDGET_OCLASS_NAME, order=10, autoEnable=false)
public class TauchartsDocumentWidget extends AbstractTauchartsWidget<ODocument>{
	private static final long serialVersionUID = 1L;

	public TauchartsDocumentWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
	@Override
	protected AbstractTauchartsPanel newChartPanel(String id){
		return new AbstractTauchartsPanel("tauchart", getModel(), new TauchartsConfig(getWidgetDocument())) {
			@Override
			protected String getSql() {
				String sql =  super.getSql();
				ODocument doc = getModelObject();
				sql = sql.replaceAll(":doc\\b", doc.getIdentity().toString());
				return sql;
			}

			@Override
			protected String getDefaultSql() {
				ODocument doc = getModelObject();
				OClass oClass = doc.getSchemaClass();
				OProperty multiProperty = null;
				for(OProperty property : oClass.properties()) {
					if(property.getType().isLink() && property.getType().isMultiValue()) {
						multiProperty = property;
						break;
					}
				}
				if(multiProperty!=null) {
					return "select expand("+multiProperty.getName()+") from :doc";
				} else {
					return "select from :doc";
				}
				
			}
		};
	}
}
