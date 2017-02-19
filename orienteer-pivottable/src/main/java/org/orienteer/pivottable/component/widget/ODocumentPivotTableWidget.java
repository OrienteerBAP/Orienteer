package org.orienteer.pivottable.component.widget;

import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.Widget;
import org.orienteer.pivottable.PivotTableModule;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;


/**
 * Widget for Pivot widget for document page
 */
@Widget(id="document-pivot-table", domain="document", oClass=PivotTableModule.WIDGET_OCLASS_NAME, order=10, autoEnable=false)
public class ODocumentPivotTableWidget extends AbstractPivotTableWidget<ODocument> {

	public ODocumentPivotTableWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
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

}
