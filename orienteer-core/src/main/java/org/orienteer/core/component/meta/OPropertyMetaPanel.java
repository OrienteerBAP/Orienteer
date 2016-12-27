package org.orienteer.core.component.meta;

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
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.behavior.RefreshMetaContextOnChangeBehaviour;
import org.orienteer.core.component.property.BooleanViewPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OClassViewPanel;
import org.orienteer.core.component.property.OPropertyViewPanel;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.model.ListAvailableOTypesModel;
import org.orienteer.core.model.ListOClassesModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.ListOPropertiesModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.utils.OClassChoiceRenderer;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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

/**
 * Meta panel for {@link OProperty}
 *
 * @param <V> type of a value
 */
public class OPropertyMetaPanel<V> extends AbstractComplexModeMetaPanel<OProperty, DisplayMode, String, V> implements IDisplayModeAware
{
	public static final List<String> OPROPERTY_ATTRS = new ArrayList<String>();
	public static final List<OType> LINKED_TYPE_OPTIONS;
	
	static
	{
		OPROPERTY_ATTRS.add(OPropertyPrototyper.NAME);
		OPROPERTY_ATTRS.add(CustomAttribute.DESCRIPTION.getName());
		OPROPERTY_ATTRS.add(CustomAttribute.TAB.getName());
		OPROPERTY_ATTRS.add(CustomAttribute.ORDER.getName());
		OPROPERTY_ATTRS.add(OPropertyPrototyper.TYPE);
		OPROPERTY_ATTRS.add(CustomAttribute.VISUALIZATION_TYPE.getName());
		OPROPERTY_ATTRS.add(OPropertyPrototyper.LINKED_TYPE);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.LINKED_CLASS);
		OPROPERTY_ATTRS.add(CustomAttribute.PROP_INVERSE.getName());
		OPROPERTY_ATTRS.add(OPropertyPrototyper.MANDATORY);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.READONLY);
		OPROPERTY_ATTRS.add(CustomAttribute.UI_READONLY.getName());
		OPROPERTY_ATTRS.add(OPropertyPrototyper.NOT_NULL);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.MIN);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.MAX);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.REGEXP);
		OPROPERTY_ATTRS.add(OPropertyPrototyper.COLLATE);
		OPROPERTY_ATTRS.add(CustomAttribute.DISPLAYABLE.getName());
		OPROPERTY_ATTRS.add(CustomAttribute.HIDDEN.getName());
		OPROPERTY_ATTRS.add(CustomAttribute.CALCULABLE.getName());
		OPROPERTY_ATTRS.add(CustomAttribute.CALC_SCRIPT.getName());
		OPROPERTY_ATTRS.add(OPropertyPrototyper.DEFAULT_VALUE);

		// Only single value types are allowed for linked type.
		LINKED_TYPE_OPTIONS = ListAvailableOTypesModel.orderTypes(Collections2.filter(Arrays.asList(OType.values()), new Predicate<OType>() {

			@Override
			public boolean apply(OType input) {
				return !input.isMultiValue();
			}
		}));
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
		CustomAttribute custom;
		if(OPropertyPrototyper.COLLATE.equals(critery))
		{
			OCollate collate = entity.getCollate();
			return (V)(collate!=null?collate.getName():null);
		}
		else if((custom = CustomAttribute.getIfExists(critery))!=null)
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
			CustomAttribute custom;
			if(OPropertyPrototyper.COLLATE.equals(critery))
			{
				entity.setCollate((String)value);
			}
			else if((custom = CustomAttribute.getIfExists(critery))!=null)
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
			// Show Linked Class if type is a some kind of link
			AbstractMetaPanel<OProperty, String, ?> metaPanel = getMetaComponent(OPropertyPrototyper.LINKED_CLASS);
			if(metaPanel!=null) metaPanel.setVisibilityAllowed(oType!=null && (oType.isLink() || oType.isEmbedded()));
			
			// Show Linked Type if type is a some kind of embedded
			metaPanel = getMetaComponent(OPropertyPrototyper.LINKED_TYPE);
			if(metaPanel!=null) metaPanel.setVisibilityAllowed(oType!=null && oType.isEmbedded() && !OType.EMBEDDED.equals(oType));
			
			// Show inverse if current type is a link
			metaPanel = getMetaComponent(CustomAttribute.PROP_INVERSE.getName());
			if(metaPanel!=null) metaPanel.setVisibilityAllowed(oType!=null && oType.isLink());
		}
		else if(CustomAttribute.CALCULABLE.getName().equals(critery))
		{
			Boolean calculable = (Boolean) getEnteredValue();
			AbstractMetaPanel<OProperty, String, ?> metaPanel = getMetaComponent(CustomAttribute.CALC_SCRIPT.getName());
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
			else if(CustomAttribute.match(critery, CustomAttribute.PROP_INVERSE)) {
				return new OPropertyViewPanel(id, (IModel<OProperty>)getModel());
			}
			else if(CustomAttribute.match(critery, CustomAttribute.CALC_SCRIPT))
			{
				return new MultiLineLabel(id, getModel());
			}
			if(OPropertyPrototyper.MANDATORY.equals(critery) 
					|| OPropertyPrototyper.READONLY.equals(critery) 
					|| OPropertyPrototyper.NOT_NULL.equals(critery)
					|| CustomAttribute.match(critery, CustomAttribute.UI_READONLY, 
													   CustomAttribute.DISPLAYABLE,
													   CustomAttribute.CALCULABLE,
													   CustomAttribute.HIDDEN))
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
				return new TextField<V>(id, getModel()).setType(String.class).setRequired(true)
										.add((IValidator<V>)OSchemaNamesValidator.FIELD_NAME_VALIDATOR).setRequired(true);
			}
			else if(OPropertyPrototyper.TYPE.equals(critery))
			{
				return new DropDownChoice<OType>(id, (IModel<OType>)getModel(), new ListAvailableOTypesModel(getEntityModel()))
						.setRequired(true)
						.add(new RefreshMetaContextOnChangeBehaviour());
			}
			else if(OPropertyPrototyper.LINKED_TYPE.equals(critery))
			{
				return new DropDownChoice<OType>(id, (IModel<OType>)getModel(), LINKED_TYPE_OPTIONS).setNullValid(true);
			}
			else if(OPropertyPrototyper.LINKED_CLASS.equals(critery))
			{
				return new DropDownChoice<OClass>(id, (IModel<OClass>)getModel(), new ListOClassesModel(), OClassChoiceRenderer.INSTANCE)
																.setNullValid(true).add(new RefreshMetaContextOnChangeBehaviour());
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
			else if(OPropertyPrototyper.MIN.equals(critery)
					|| OPropertyPrototyper.MAX.equals(critery)
					|| OPropertyPrototyper.REGEXP.equals(critery)
					|| OPropertyPrototyper.DEFAULT_VALUE.equals(critery))
			{
				return new TextField<V>(id, getModel());
			}
			else
			{
				final CustomAttribute customAttr = CustomAttribute.getIfExists(critery);
				
				if(customAttr!=null)
				{
					if(customAttr.equals(CustomAttribute.CALCULABLE)) {
						return new CheckBox(id, (IModel<Boolean>)getModel()).add(new RefreshMetaContextOnChangeBehaviour());
					} else if(customAttr.matchAny(CustomAttribute.DISPLAYABLE, CustomAttribute.HIDDEN, CustomAttribute.UI_READONLY)) {
						return new CheckBox(id, (IModel<Boolean>)getModel());
					} else if(customAttr.matchAny(CustomAttribute.CALC_SCRIPT, CustomAttribute.DESCRIPTION)) {
						return new TextArea<V>(id, getModel());
					} else if(customAttr.equals(CustomAttribute.ORDER)) {
						return new TextField<V>(id, getModel()).setType(Integer.class);
					} else if(customAttr.equals(CustomAttribute.TAB)) {
						return new TextField<V>(id, getModel());
					} else if(customAttr.equals(CustomAttribute.VISUALIZATION_TYPE)) {
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
					} else if(customAttr.equals(CustomAttribute.PROP_INVERSE)) {
						IModel<OClass> linkedClassModel = (IModel<OClass>)getMetaComponentEnteredValueModel(OPropertyPrototyper.LINKED_CLASS);
						return new DropDownChoice<OProperty>(id, (IModel<OProperty>)getModel(),
								new ListOPropertiesModel(linkedClassModel, null)
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
