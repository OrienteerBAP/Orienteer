package ru.ydn.orienteer.schema;

import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.CustomAttributes;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class SchemaHelper 
{
	public static boolean isPropertyCalculable(OProperty property)
	{
		if(property==null) return false;
		String calcFlag = property.getCustom(CustomAttributes.CALCULABLE.getName());
		return Strings.isEqual(calcFlag, "true");
	}

}
