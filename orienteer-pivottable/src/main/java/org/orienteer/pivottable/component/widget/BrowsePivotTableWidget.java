package org.orienteer.pivottable.component.widget;

import java.util.Collection;
import java.util.Locale;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.Widget;
import org.orienteer.pivottable.PivotTableModule;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;


/**
 * Widget for Pivot widget for browse page
 */
@Widget(id="pivot-table", domain="browse", oClass=PivotTableModule.WIDGET_OCLASS_NAME, order=10, autoEnable=false)
public class BrowsePivotTableWidget extends AbstractPivotTableWidget<OClass> {
	
	@Inject
	private IOClassIntrospector oClassIntrospector;

	public BrowsePivotTableWidget(String id, IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
	@Override
	public String getDefaultSql() {
		String thisLang = getLocale().getLanguage();
		String systemLang = Locale.getDefault().getLanguage();
		OClass oClass = getModelObject();
		StringBuilder sb = new StringBuilder();
		Collection<OProperty> properties = oClass.properties();
		for(OProperty property: properties) {
			OType type = property.getType();
			if(Comparable.class.isAssignableFrom(type.getDefaultJavaType())) {
				sb.append(property.getName()).append(", ");
			} else if(OType.LINK.equals(type)) {
				OClass linkedClass = property.getLinkedClass();
				OProperty nameProperty = oClassIntrospector.getNameProperty(linkedClass);
				if(nameProperty!=null) {
					OType linkedClassType = nameProperty.getType();
					String map = property.getName()+'.'+nameProperty.getName();
					if(Comparable.class.isAssignableFrom(linkedClassType.getDefaultJavaType())) {
						sb.append(map).append(", ");
					} else if (OType.EMBEDDEDMAP.equals(linkedClassType)) {
						sb.append("coalesce(").append(map).append('[').append(thisLang).append("], ");
						if(!thisLang.equals(systemLang)) {
							sb.append(map).append('[').append(systemLang).append("], ");
						}
						sb.append("first(").append(map).append(")) as ").append(property.getName()).append(", ");
					}
				}
			}
		}
		if(sb.length()>0) sb.setLength(sb.length()-2);
		sb.insert(0, "SELECT ");
		sb.append(" FROM ").append(oClass.getName());
		return sb.toString();
	}

}
