package org.orienteer.model;

import java.util.Collection;

import org.apache.wicket.model.IModel;
import org.orienteer.CustomAttributes;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;

public class ExtendedOPropertiesDataProvider extends OPropertiesDataProvider
{

	public ExtendedOPropertiesDataProvider(
			IModel<Collection<OProperty>> dataModel)
	{
		super(dataModel);
	}

	public ExtendedOPropertiesDataProvider(IModel<OClass> oClassModel,
			IModel<Boolean> allPropertiesModel)
	{
		super(oClassModel, allPropertiesModel);
	}

	public ExtendedOPropertiesDataProvider(OClass oClass, boolean allProperties)
	{
		super(oClass, allProperties);
	}

	@Override
	protected Comparable<?> comparableValue(OProperty input, String sortParam) {
		CustomAttributes custom = CustomAttributes.fromString(sortParam);
		if(custom!=null)
		{
			Object value = custom.getValue(input);
			return value instanceof Comparable?(Comparable<?>)value:null;
		}
		else return super.comparableValue(input, sortParam);
	}

	@Override
	protected String getSortPropertyExpression(String param) {
		if(OPropertyPrototyper.LINKED_CLASS.equals(param)) return param+".name";
		else return super.getSortPropertyExpression(param);
	}
	
	

}
