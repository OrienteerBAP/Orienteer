package org.orienteer.core.model;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.xmpbox.schema.ExifSchema;

import com.orientechnologies.orient.core.metadata.schema.OProperty;

import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

/**
 * Model to provide tooltip for a property 
 */
public class OPropertyTooltipModel extends OPropertyNamingModel {
	
	private static final long serialVersionUID = 1L;

	public OPropertyTooltipModel(OProperty oProperty)
	{
		super(oProperty);
	}

	public OPropertyTooltipModel(IModel<OProperty> objectModel) {
		super(objectModel);
	}
	
	@Override
	public String getObject(Component component) {
		String ret = super.getObject(component);
		return ret!=null && !ret.equals("$tooltip") ? ret : null;
	}

	@Override
	public String getResourceKey(OProperty object) {
		return object.getFullName()+".$tooltip";
	}
	
	@Override
	public String getDefault() {
		return "$tooltip";
	}
}
