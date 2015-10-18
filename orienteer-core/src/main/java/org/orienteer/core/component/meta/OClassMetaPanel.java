package org.orienteer.core.component.meta;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OBalancedClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ODefaultClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ORoundRobinClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.metadata.security.OSecurityShared;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;
import org.orienteer.core.CustomAttributes;
import org.orienteer.core.behavior.RefreshMetaContextOnChangeBehaviour;
import org.orienteer.core.component.property.*;
import org.orienteer.core.model.OClassTextChoiceProvider;
import org.orienteer.core.model.OnCreateFieldsTextChoiceProvider;
import org.wicketstuff.select2.DragAndDropBehavior;
import org.wicketstuff.select2.Select2MultiChoice;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.ListOPropertiesModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Meta panel for {@link OClass}
 *
 * @param <V> type of a value
 */
public class OClassMetaPanel<V> extends AbstractComplexModeMetaPanel<OClass, DisplayMode, String, V> implements IDisplayModeAware
{
	public static final List<String> OCLASS_ATTRS = new ArrayList<String>(OClassPrototyper.OCLASS_ATTRS);
	static
	{
		//Index:OCLASS_ATTRS.indexOf(OClassPrototyper.NAME)+1
		OCLASS_ATTRS.add(2, CustomAttributes.DESCRIPTION.getName());
		OCLASS_ATTRS.add(CustomAttributes.PROP_NAME.getName());
		OCLASS_ATTRS.add(CustomAttributes.PROP_PARENT.getName());
		OCLASS_ATTRS.add(CustomAttributes.TAB.getName());
		OCLASS_ATTRS.add(CustomAttributes.ON_CREATE_FIELDS.getName());
		OCLASS_ATTRS.add(CustomAttributes.ON_CREATE_IDENTITY_TYPE.getName());
	}
	
	private static final Predicate<OProperty> IS_LINK_PROPERTY = new Predicate<OProperty>() {

		@Override
		public boolean apply(OProperty input) {
			return OType.LINK.equals(input.getType());
		}
	};
	
	private static final long serialVersionUID = 1L;
	private static final List<String> CLUSTER_SELECTIONS = 
			Arrays.asList(new String[]{ODefaultClusterSelectionStrategy.NAME, ORoundRobinClusterSelectionStrategy.NAME, OBalancedClusterSelectionStrategy.NAME});

	private static final List<String> ON_CREATE_IDENTITY_SELECTIONS =
			Arrays.asList(new String[]{"user", "role"});
	
	public OClassMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<OClass> entityModel, IModel<String> criteryModel) {
		super(id, modeModel, entityModel, criteryModel);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected V getValue(OClass entity, String critery) {
		CustomAttributes custom;
		if("clusterSelection".equals(critery))
		{
			OClusterSelectionStrategy strategy = entity.getClusterSelection();
			return (V)(strategy!=null?strategy.getName():null);
		}
		else if(OClassPrototyper.SUPER_CLASSES.equals(critery))
		{
			List<OClass> superClasses = entity.getSuperClasses();
			// Additional wrapping to ArrayList is required , because getSuperClasses return unmodifiable list
			return (V)(superClasses != null ? new ArrayList<OClass>(superClasses) : new ArrayList<OClass>());
		}
		else if((CustomAttributes.ON_CREATE_FIELDS.getName().equals(critery)) && (custom = CustomAttributes.fromString(critery)) != null)
		{
			String onCreateFields = custom.getValue(entity);
			return (V)(!Strings.isNullOrEmpty(onCreateFields)
					? Lists.newArrayList(onCreateFields.split(","))
					: new ArrayList<String>());
		}
		else if((custom = CustomAttributes.fromString(critery))!=null)
		{
			return custom.getValue(entity);
		}
		else
		{
			return (V)PropertyResolver.getValue(critery, entity);
		}
	}

	@Override
	protected void setValue(OClass entity, String critery, V value) {
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		db.commit();
		try
		{
			CustomAttributes custom;
			if(OClassPrototyper.CLUSTER_SELECTION.equals(critery))
			{
				if(value!=null) entity.setClusterSelection(value.toString());
			}
			else if((CustomAttributes.ON_CREATE_FIELDS.getName().equals(critery)) && (custom = CustomAttributes.fromString(critery)) != null)
			{
				custom.setValue(entity, value!=null?Joiner.on(",").join((List<String>) value):null);
			}
			else if((custom = CustomAttributes.fromString(critery))!=null)
			{
				custom.setValue(entity, value);
			}
			else if (OClassPrototyper.SUPER_CLASSES.equals(critery))
			{
				if(value!=null) entity.setSuperClasses((List<OClass>) value);
			}
			else
			{
				PropertyResolver.setValue(critery, entity, value, new PropertyResolverConverter(Application.get().getConverterLocator(),
						Session.get().getLocale()));
			}
		} finally
		{
			db.begin();
		}
	}





	@SuppressWarnings("unchecked")
	@Override
	protected Component resolveComponent(String id, DisplayMode mode,
			String critery) {
		if(DisplayMode.EDIT.equals(mode) && !OSecurityHelper.isAllowed(ORule.ResourceGeneric.SCHEMA, null, OrientPermission.UPDATE))
		{
			mode = DisplayMode.VIEW;
		}
		if(DisplayMode.VIEW.equals(mode))
		{
			if(CustomAttributes.match(critery, CustomAttributes.PROP_NAME, CustomAttributes.PROP_PARENT))
			{
				return new OPropertyViewPanel(id, (IModel<OProperty>)getModel());
			}
			else if(OClassPrototyper.SUPER_CLASSES.equals(critery)) {
				return new MultipleOClassesViewPanel(id, (IModel<List<OClass>>)getModel());
			}
			if(OClassPrototyper.ABSTRACT.equals(critery) || OClassPrototyper.STRICT_MODE.equals(critery))
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
				if(OClassPrototyper.NAME.equals(critery) || OClassPrototyper.SHORT_NAME.equals(critery))
				{
					return new TextField<V>(id, getModel()).setType(String.class).add((IValidator<V>)OSchemaNamesValidator.CLASS_NAME_VALIDATOR);
				}
				else if(OClassPrototyper.ABSTRACT.equals(critery) || OClassPrototyper.STRICT_MODE.equals(critery))
				{
					return new BooleanEditPanel(id, (IModel<Boolean>)getModel());
				}
				else if(OClassPrototyper.OVER_SIZE.equals(critery))
				{
					return new TextField<V>(id, getModel()).setType(Float.class);
				}
				else if(OClassPrototyper.SUPER_CLASSES.equals(critery))
 				{
					return new Select2MultiChoice<OClass>(id, (IModel<Collection<OClass>>)getModel(), OClassTextChoiceProvider.INSTANCE)
							.add(new DragAndDropBehavior())
							.add(new RefreshMetaContextOnChangeBehaviour());
				}
				else if(OClassPrototyper.CLUSTER_SELECTION.equals(critery))
				{
					return new DropDownChoice<String>(id, (IModel<String>)getModel(), CLUSTER_SELECTIONS);
				}
				else if(CustomAttributes.match(critery, CustomAttributes.PROP_NAME))
				{
					return new DropDownChoice<OProperty>(id, (IModel<OProperty>)getModel(), new ListOPropertiesModel(getEntityModel(), null));
				}
				else if(CustomAttributes.match(critery, CustomAttributes.PROP_PARENT))
				{
					return new DropDownChoice<OProperty>(id, (IModel<OProperty>)getModel(), new ListOPropertiesModel(getEntityModel(), null) {
						
						@Override
						protected Predicate<? super OProperty> getFilterPredicate() {
							return IS_LINK_PROPERTY;
						}

					}).setNullValid(true);
				}
                else if(CustomAttributes.match(critery,CustomAttributes.DESCRIPTION))
                {
                    return new TextArea<V>(id, getModel());
                }
                else if (CustomAttributes.match(critery,CustomAttributes.TAB))
                {
                    return new TextField<V>(id,getModel());
                }
				else if(CustomAttributes.match(critery, CustomAttributes.ON_CREATE_FIELDS))
				{
					return new Select2MultiChoice<String>(id, (IModel<Collection<String>>)getModel(), OnCreateFieldsTextChoiceProvider.INSTANCE);
				}
				else if(CustomAttributes.match(critery, CustomAttributes.ON_CREATE_IDENTITY_TYPE))
				{
					return new DropDownChoice<String>(id, (IModel<String>)getModel(), ON_CREATE_IDENTITY_SELECTIONS).setNullValid(true);
				}
				else
				{
					return new Label(id, getModel());
				}
		}
		else return null;
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		String critery = getPropertyObject();
		if(OClassPrototyper.SUPER_CLASSES.equals(critery))
		{
			Collection<OClass> superClasses = (Collection<OClass>)getEnteredValue();
			AbstractMetaPanel<OClass, String, ?> onCreateFieldsPanel = getMetaComponent(CustomAttributes.ON_CREATE_FIELDS.getName());
			AbstractMetaPanel<OClass, String, ?> onCreateIdentityTypePanel = getMetaComponent(CustomAttributes.ON_CREATE_IDENTITY_TYPE.getName());
			if(onCreateFieldsPanel!=null || onCreateIdentityTypePanel!=null) {
				boolean visibility = false;
				for(OClass superClass : superClasses) {
					if(visibility = superClass.isSubClassOf(OSecurityShared.RESTRICTED_CLASSNAME)) break;
				}
				if(onCreateFieldsPanel!=null) onCreateFieldsPanel.setVisibilityAllowed(visibility);
				if(onCreateIdentityTypePanel!=null) onCreateIdentityTypePanel.setVisibilityAllowed(visibility);
			}
		}
	}

	@Override
	public IModel<String> newLabelModel() {
		return new SimpleNamingModel<String>("class", getPropertyModel());
	}
	

}
