package ru.ydn.orienteer.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.components.properties.LinkViewPanel;
import ru.ydn.orienteer.schema.SchemaHelper;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OEntityColumn<T> extends PropertyColumn<T, String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OEntityColumn(OClass oClass)
	{
		this(oClass, "");
	}
	
	public OEntityColumn(String oClass)
	{
		this(oClass, "");
	}
	public OEntityColumn(IModel<String> displayModel, OClass oClass) {
		this(displayModel, oClass, "");
	}
	public OEntityColumn(IModel<String> displayModel, String oClass) {
		this(displayModel, oClass, "");
	}
	
	public OEntityColumn(OClass oClass, String propertyExpression)
	{
		this(new OClassNamingModel(oClass), oClass, propertyExpression);
	}
	public OEntityColumn(String oClass, String propertyExpression)
	{
		this(new OClassNamingModel(oClass), oClass, propertyExpression);
	}
	public OEntityColumn(IModel<String> displayModel, OClass oClass, String propertyExpression) {
		super(displayModel, SchemaHelper.resolveNameProperty(oClass), propertyExpression);
	}
	public OEntityColumn(IModel<String> displayModel, String oClass, String propertyExpression) {
		super(displayModel, SchemaHelper.resolveNameProperty(oClass), propertyExpression);
	}
	
	public String getNameProperty()
	{
		return getSortProperty();
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem,
			String componentId, IModel<T> rowModel) {
		cellItem.add(new LinkViewPanel<ODocument>(componentId, getDocumentModel(rowModel)));
	}
	
	public IModel<ODocument> getDocumentModel(IModel<T> rowModel)
	{
		if(Strings.isEmpty(getPropertyExpression())) return (IModel<ODocument>)rowModel;
		else return new PropertyModel<ODocument>(rowModel, getPropertyExpression());
	}

}
