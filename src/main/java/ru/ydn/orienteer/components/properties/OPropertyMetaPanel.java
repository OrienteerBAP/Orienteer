package ru.ydn.orienteer.components.properties;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.wicket.Component;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidator;

import ru.ydn.orienteer.components.properties.OClassMetaPanel.ListClassesModel;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class OPropertyMetaPanel<V> extends AbstractComplexModeMetaPanel<OProperty, DisplayMode, String, V>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OPropertyMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<OProperty> entityModel, IModel<String> propertyModel,
			IModel<V> valueModel)
	{
		super(id, modeModel, entityModel, propertyModel, valueModel);
	}

	public OPropertyMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<OProperty> entityModel, IModel<String> propertyModel)
	{
		super(id, modeModel, entityModel, propertyModel);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected V getValue(OProperty entity, String critery) {
		return (V) PropertyResolver.getValue(critery, entity);
	}

	@Override
	protected void setValue(OProperty entity, String critery, V value) {
		PropertyResolver.setValue(critery, entity, value, null);
	}

	@Override
	protected Component resolveComponent(String id, DisplayMode mode,
			String critery) {
		if(DisplayMode.VIEW.equals(mode))
		{
			if("linkedClass".equals(critery))
			{
				return new OClassViewPanel(id, (IModel<OClass>)getModel());
			}
			else if("collate".equals(critery))
			{
				return new Label(id, new PropertyModel<String>(getModel(), "name"));
			}
			else
			{
				return new Label(id, getModel());
			}
		}
		else if(DisplayMode.EDIT.equals(mode))
		{
			if("name".equals(critery))
			{
				return new TextField<V>(id, getModel()).setType(String.class).add((IValidator<V>)OSchemaNamesValidator.INSTANCE).setRequired(true);
			}
			else if("type".equals(critery))
			{
				return new DropDownChoice<OType>(id, (IModel<OType>)getModel(), Arrays.asList(OType.values())).setRequired(true);
			}
			else if("linkedType".equals(critery))
			{
				return new DropDownChoice<OType>(id, (IModel<OType>)getModel(), Arrays.asList(OType.values())).setNullValid(true);
			}
			else if("linkedClass".equals(critery))
			{
				return new DropDownChoice<OClass>(id, (IModel<OClass>)getModel(), new ListClassesModel(), new IChoiceRenderer<OClass>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public Object getDisplayValue(OClass object) {
						return new OClassNamingModel(object).getObject();
					}

					@Override
					public String getIdValue(OClass object, int index) {
						return object.getName();
					}
				}).setNullValid(true);
			}
			else if("mandatory".equals(critery) || "readonly".equals(critery) || "notNull".equals(critery))
			{
				return new CheckBox(id, (IModel<Boolean>)getModel());
			}
			else if("min".equals(critery) || "max".equals(critery))
			{
				return new TextField<V>(id, getModel());
			}
			else
			{
				return resolveComponent(id, DisplayMode.VIEW, critery);
			}
		}
		else return null;
	}

	@Override
	public IModel<String> newLabelModel() {
		return new AbstractNamingModel<String>(getPropertyModel()) {

			@Override
			public String getResourceKey(String object) {
				return "property."+object;
			}
		};
	}
	

}
