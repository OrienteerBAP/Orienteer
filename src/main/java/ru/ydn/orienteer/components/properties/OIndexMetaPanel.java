package ru.ydn.orienteer.components.properties;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OIndexMetaPanel<V> extends AbstractComplexModeMetaPanel<OIndex<?>, DisplayMode, String, V>
{
	
	

	public OIndexMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<OIndex<?>> entityModel, IModel<String> propertyModel,
			IModel<V> valueModel)
	{
		super(id, modeModel, entityModel, propertyModel, valueModel);
	}

	public OIndexMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<OIndex<?>> entityModel, IModel<String> criteryModel)
	{
		super(id, modeModel, entityModel, criteryModel);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected V getValue(OIndex<?> entity, String critery) {
		return (V) PropertyResolver.getValue(critery, entity);
	}

	@Override
	protected void setValue(OIndex<?> entity, String critery, V value) {
		PropertyResolver.setValue(critery, entity, value, null);
	}
	
	@Override
	protected Component resolveComponent(String id, DisplayMode mode,
			String critery) {
		if(DisplayMode.VIEW.equals(mode))
		{
					if(OIndexPrototyper.DEF_CLASS_NAME.equals(critery))
					{
						return new OClassViewPanel(id, new OClassModel((IModel<String>)getModel()));
					}
					else
					{
						return new Label(id, getModel());
					}
		}
		else if(DisplayMode.EDIT.equals(mode))
		{
					return new Label(id, getModel());
		}
		else return null;
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new AbstractNamingModel<String>(getPropertyModel()) {

			@Override
			public String getResourceKey(String object) {
				return "index."+object;
			}
		};
	}

}
