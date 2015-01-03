package ru.ydn.orienteer.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.LinkViewPanel;
import ru.ydn.orienteer.schema.SchemaHelper;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OEntityColumn extends OPropertyValueColumn
{
	private static final long serialVersionUID = 1L;
	
	public OEntityColumn(OClass oClass, IModel<DisplayMode> modeModel)
	{
		this(SchemaHelper.resolveNameProperty(oClass), true, modeModel);
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
	public void populateItem(Item<ICellPopulator<ODocument>> cellItem,
			String componentId, IModel<ODocument> rowModel) {
		if(DisplayMode.VIEW.equals(getModeObject()))
		{
			cellItem.add(new LinkViewPanel<ODocument>(componentId, rowModel));
		}
		else
		{
			super.populateItem(cellItem, componentId, rowModel);
		}
	}
	
}
