package ru.ydn.orienteer.components.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidator;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.components.properties.OClassMetaPanel.ListClassesModel;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.orientechnologies.orient.core.collate.OCollate;
import com.orientechnologies.orient.core.collate.OCollateFactory;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OSQLEngine;

public class OPropertyMetaPanel<V> extends AbstractComplexModeMetaPanel<OProperty, DisplayMode, String, V>
{
	public static final List<String> OPROPERTY_ATTRS = new ArrayList<String>(OPropertyPrototyper.OPROPERTY_ATTRS);
	static
	{
		OPROPERTY_ATTRS.add(CustomAttributes.CALCULABLE.getName());
		OPROPERTY_ATTRS.add(CustomAttributes.CALC_SCRIPT.getName());
		OPROPERTY_ATTRS.add(CustomAttributes.DISPLAYABLE.getName());
		OPROPERTY_ATTRS.add(CustomAttributes.ORDER.getName());
		OPROPERTY_ATTRS.add(CustomAttributes.TAB.getName());
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static class OPropertyFieldNameModel extends AbstractNamingModel<String> 
	{
		public OPropertyFieldNameModel(IModel<String> objectModel)
		{
			super(objectModel);
		}

		public OPropertyFieldNameModel(String object)
		{
			super(object);
		}

		@Override
		public String getResourceKey(String object) {
			return "property."+object;
		}
	}

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
		CustomAttributes custom;
		if(OPropertyPrototyper.COLLATE.equals(critery))
		{
			OCollate collate = entity.getCollate();
			return (V)(collate!=null?collate.getName():null);
		}
		else if((custom = CustomAttributes.fromString(critery))!=null)
		{
			return custom.getValue(entity);
		}
		else
		{
			return (V) PropertyResolver.getValue(critery, entity);
		}
	}

	@Override
	protected void setValue(OProperty entity, String critery, V value) {
		CustomAttributes custom;
		if(OPropertyPrototyper.COLLATE.equals(critery))
		{
			entity.setCollate((String)value);
		}
		else if((custom = CustomAttributes.fromString(critery))!=null)
		{
			custom.setValue(entity, value);
		}
		else
		{
			PropertyResolver.setValue(critery, entity, value, null);
		}
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		String critery = getPropertyObject();
		if("type".equals(critery))
		{
			OType oType = (OType)getEnteredValue();
			getMetaComponent("linkedClass").setVisibilityAllowed(oType!=null && oType.isLink());
		}
	}

	@SuppressWarnings("unchecked")
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
				return new Label(id, getModel());
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
				return new DropDownChoice<OType>(id, (IModel<OType>)getModel(), Arrays.asList(OType.values()))
						.setRequired(true)
						.add(new OnChangeAjaxBehavior() {
							
							@Override
							protected void onUpdate(AjaxRequestTarget target) {
								target.add(OPropertyMetaPanel.this.getMetaContext().getContextComponent());
							}

							/*
							 TODO: Comment this till fix of WICKET-5658
							@Override
							protected boolean getUpdateModel() {
								return false;
							}*/
							
						});
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
			else if("collate".equals(critery))
			{
				return new DropDownChoice<String>(id, (IModel<String>)getModel(), Lists.newArrayList(OSQLEngine.getCollateNames()));
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
				CustomAttributes customAttr = CustomAttributes.fromString(critery);
				if(customAttr!=null)
				{
					switch (customAttr) {
					case CALCULABLE:
					case DISPLAYABLE:
						return new CheckBox(id, (IModel<Boolean>)getModel());
					case CALC_SCRIPT:
						return new TextArea<V>(id, getModel());
					case ORDER:
						return new TextField<V>(id, getModel()).setType(Integer.class);
					case TAB:
						return new TextField<V>(id, getModel());
					}
				}
				return resolveComponent(id, DisplayMode.VIEW, critery);
			}
		}
		else return null;
	}

	@Override
	public IModel<String> newLabelModel() {
		return new OPropertyFieldNameModel(getPropertyModel());
	}
	

}
