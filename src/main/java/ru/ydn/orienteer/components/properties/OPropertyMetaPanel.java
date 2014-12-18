package ru.ydn.orienteer.components.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidator;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.behavior.RefreshMetaContextOnChangeBehaviour;
import ru.ydn.orienteer.components.properties.OClassMetaPanel.ListClassesModel;
import ru.ydn.orienteer.model.ListAvailableOTypesModel;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.ListOPropertiesModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.orientechnologies.common.thread.OPollerThread;
import com.orientechnologies.orient.core.collate.OCollate;
import com.orientechnologies.orient.core.collate.OCollateFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OSQLEngine;

public class OPropertyMetaPanel<V> extends AbstractComplexModeMetaPanel<OProperty, DisplayMode, String, V>
{
	public static final List<String> OPROPERTY_ATTRS = new ArrayList<String>();
	static
	{
		OPROPERTY_ATTRS.add(OPropertyPrototyper.NAME);
		OPROPERTY_ATTRS.add(CustomAttributes.TAB.getName());
		OPROPERTY_ATTRS.add(CustomAttributes.ORDER.getName());
		OPROPERTY_ATTRS.add(OPropertyPrototyper.TYPE);
		OPROPERTY_ATTRS.add(CustomAttributes.VISUALIZATION_TYPE.getName());
		OPROPERTY_ATTRS.add(OPropertyPrototyper.LINKED_TYPE);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.LINKED_CLASS);
		OPROPERTY_ATTRS.add(CustomAttributes.PROP_INVERSE.getName());
		OPROPERTY_ATTRS.add(OPropertyPrototyper.MANDATORY);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.READONLY);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.NOT_NULL);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.MIN);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.MAX);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.REGEXP);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.COLLATE);
		OPROPERTY_ATTRS.add(CustomAttributes.DISPLAYABLE.getName());
		OPROPERTY_ATTRS.add(CustomAttributes.HIDDEN.getName());
		OPROPERTY_ATTRS.add(CustomAttributes.CALCULABLE.getName());
		OPROPERTY_ATTRS.add(CustomAttributes.CALC_SCRIPT.getName());
	}
	private static final Predicate<OProperty> CAN_BE_INVERSE_PROPERTY = new Predicate<OProperty>() {

		@Override
		public boolean apply(OProperty input) {
			return input.getType().isLink();
		}
	};
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
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		db.commit();
		try
		{
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
		} finally
		{
			db.begin();
		}
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		String critery = getPropertyObject();
		if(OPropertyPrototyper.TYPE.equals(critery))
		{
			OType oType = (OType)getEnteredValue();
			AbstractMetaPanel<OProperty, String, ?> metaPanel = getMetaComponent(OPropertyPrototyper.LINKED_CLASS);
			if(metaPanel!=null) metaPanel.setVisibilityAllowed(oType!=null && oType.isLink());
			metaPanel = getMetaComponent(CustomAttributes.PROP_INVERSE.getName());
			if(metaPanel!=null) metaPanel.setVisibilityAllowed(oType!=null && oType.isLink());
		}
		else if(CustomAttributes.CALCULABLE.getName().equals(critery))
		{
			Boolean calculable = (Boolean) getEnteredValue();
			AbstractMetaPanel<OProperty, String, ?> metaPanel = getMetaComponent(CustomAttributes.CALC_SCRIPT.getName());
			if(metaPanel!=null) metaPanel.setVisibilityAllowed(calculable!=null && calculable);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Component resolveComponent(String id, DisplayMode mode,
			String critery) {
		if(DisplayMode.VIEW.equals(mode))
		{
			if(OPropertyPrototyper.LINKED_CLASS.equals(critery))
			{
				return new OClassViewPanel(id, (IModel<OClass>)getModel());
			}
			else if(OPropertyPrototyper.COLLATE.equals(critery))
			{
				return new Label(id, getModel());
			}
			else if(CustomAttributes.match(critery, CustomAttributes.PROP_INVERSE))
			{
				return new OPropertyViewPanel(id, (IModel<OProperty>)getModel());
			}
			else if(CustomAttributes.match(critery, CustomAttributes.CALC_SCRIPT))
			{
				return new MultiLineLabel(id, getModel());
			}
			if(OPropertyPrototyper.MANDATORY.equals(critery) 
					|| OPropertyPrototyper.READONLY.equals(critery) 
					|| OPropertyPrototyper.NOT_NULL.equals(critery)
					|| CustomAttributes.match(critery, CustomAttributes.DISPLAYABLE, CustomAttributes.CALCULABLE))
			{
				return new BooleanViewPanel(id, (IModel<Boolean>)getModel()).setHideIfFalse(true);
			}
			else
			{
				return new Label(id, getModel());
			}
		}
		else if(DisplayMode.EDIT.equals(mode))
		{
			if(OPropertyPrototyper.NAME.equals(critery))
			{
				return new TextField<V>(id, getModel()).setType(String.class).add((IValidator<V>)OSchemaNamesValidator.INSTANCE).setRequired(true);
			}
			else if(OPropertyPrototyper.TYPE.equals(critery))
			{
				return new DropDownChoice<OType>(id, (IModel<OType>)getModel(), new ListAvailableOTypesModel(getEntityModel()))
						.setRequired(true)
						.add(new RefreshMetaContextOnChangeBehaviour());
			}
			else if(OPropertyPrototyper.LINKED_TYPE.equals(critery))
			{
				return new DropDownChoice<OType>(id, (IModel<OType>)getModel(), Arrays.asList(OType.values())).setNullValid(true);
			}
			else if(OPropertyPrototyper.LINKED_CLASS.equals(critery))
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
				}).setNullValid(true).add(new RefreshMetaContextOnChangeBehaviour());
			}
			else if(OPropertyPrototyper.COLLATE.equals(critery))
			{
				return new DropDownChoice<String>(id, (IModel<String>)getModel(), Lists.newArrayList(OSQLEngine.getCollateNames()));
			}
			else if(OPropertyPrototyper.MANDATORY.equals(critery) 
					|| OPropertyPrototyper.READONLY.equals(critery) 
					|| OPropertyPrototyper.NOT_NULL.equals(critery))
			{
				return new CheckBox(id, (IModel<Boolean>)getModel());
			}
			else if(OPropertyPrototyper.MIN.equals(critery) || OPropertyPrototyper.MAX.equals(critery) || OPropertyPrototyper.REGEXP.equals(critery))
			{
				return new TextField<V>(id, getModel());
			}
			else
			{
				final CustomAttributes customAttr = CustomAttributes.fromString(critery);
				if(customAttr!=null)
				{
					switch (customAttr) {
					case CALCULABLE:
						return new CheckBox(id, (IModel<Boolean>)getModel()).add(new RefreshMetaContextOnChangeBehaviour());
					case DISPLAYABLE:
					case HIDDEN:
						return new CheckBox(id, (IModel<Boolean>)getModel());
					case CALC_SCRIPT:
						return new TextArea<V>(id, getModel());
					case ORDER:
						return new TextField<V>(id, getModel()).setType(Integer.class);
					case TAB:
						return new TextField<V>(id, getModel());
					case VISUALIZATION_TYPE:
						return new DropDownChoice<String>(id,  (IModel<String>)getModel(), new LoadableDetachableModel<List<String>>() {
								@Override
								protected List<String> load() {
									OType type = getMetaComponentEnteredValue(OPropertyPrototyper.TYPE);
									UIVisualizersRegistry registry = OrienteerWebApplication.get().getUIVisualizersRegistry();
									return registry.getComponentsOptions(type);
								}
							})
						{

							private static final long serialVersionUID = 1L;

							@Override
							protected void onConfigure() {
								super.onConfigure();
								List<?> choices = getChoices();
								setVisible(choices!=null && choices.size()>0);
							}
							
						}.setNullValid(false).setRequired(true);
					case PROP_INVERSE:
						return new DropDownChoice<OProperty>(id, (IModel<OProperty>)getModel(),
									new ListOPropertiesModel((IModel<OClass>)getMetaComponentEnteredValueModel(OPropertyPrototyper.LINKED_CLASS), null)
									{
										@Override
										protected Predicate<? super OProperty> getFilterPredicate() {
											return CAN_BE_INVERSE_PROPERTY;
										}
										
									}
								).setNullValid(true);
					}
				}
				return resolveComponent(id, DisplayMode.VIEW, critery);
			}
		}
		else return null;
	}

	@Override
	public IModel<String> newLabelModel() {
		return new SimpleNamingModel<String>("property", getPropertyModel());
	}
	

}
