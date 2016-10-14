package org.orienteer.core.component.table;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.LinkViewPanel;
import org.orienteer.core.model.ODocumentNameModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link OPropertyValueColumn} to refer to {@link ODocument} themselves
 */
public class OEntityColumn extends OPropertyValueColumn
{
	private static final long serialVersionUID = 1L;
	
	public OEntityColumn(OClass oClass, IModel<DisplayMode> modeModel)
	{
		this(OrienteerWebApplication.get().getOClassIntrospector().getNameProperty(oClass), true, modeModel);
	}

	public OEntityColumn(IModel<OProperty> criteryModel,
			IModel<DisplayMode> modeModel)
	{
		super(criteryModel, modeModel);
	}
	
	public OEntityColumn(OProperty oProperty, boolean sortColumn, IModel<DisplayMode> modeModel)
	{
		super(sortColumn?resolveSortExpression(oProperty):null, oProperty, modeModel);
	}

	public OEntityColumn(OProperty oProperty, IModel<DisplayMode> modeModel)
	{
		super(oProperty, modeModel);
	}

	public OEntityColumn(String sortProperty, IModel<OProperty> criteryModel,
			IModel<DisplayMode> modeModel)
	{
		super(sortProperty, criteryModel, modeModel);
	}

	public OEntityColumn(String sortProperty, OProperty oProperty,
			IModel<DisplayMode> modeModel)
	{
		super(sortProperty, oProperty, modeModel);
	}
	
	private static String resolveSortExpression(OProperty property)
	{
		if(property==null || property.getType()==null) return null;
		Class<?> defType = property.getType().getDefaultJavaType();
		return defType!=null && Comparable.class.isAssignableFrom(defType)?property.getName():null;
	}
		
	public String getNameProperty()
	{
		return getSortProperty();
	}

	@Override
	protected <V> AbstractMetaPanel<ODocument, OProperty, V> newMetaPanel(
			String componentId, IModel<OProperty> criteryModel,
			IModel<ODocument> rowModel) {
		return new ODocumentMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel) {
			@Override
			protected Component resolveComponent(String id, DisplayMode mode,
					OProperty property) {
				if (DisplayMode.VIEW.equals(mode) || property == null)
					return new LinkViewPanel(id, getEntityModel(), new ODocumentNameModel(getEntityModel(), getCriteryModel()));
				else 
					return super.resolveComponent(id, mode, property);
			}
		};
	}
	
}
