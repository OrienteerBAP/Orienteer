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
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.behavior.RefreshMetaContextOnChangeBehaviour;
import org.orienteer.core.component.property.*;
import org.orienteer.core.model.OClassTextChoiceProvider;
import org.orienteer.core.model.OnCreateFieldsTextChoiceProvider;
import org.wicketstuff.select2.ISelect2Theme;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2MultiChoice;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.EnumNamingModel;
import ru.ydn.wicket.wicketorientdb.model.ListOPropertiesModel;
import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.utils.ResourceChoiceRenderer;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Meta panel for {@link OClass}
 *
 * @param <V> type of a value
 */
public class OClassMetaPanel<V> extends AbstractComplexModeMetaPanel<OClass, DisplayMode, String, V> implements IDisplayModeAware
{
	public static final ISelect2Theme BOOTSTRAP_SELECT2_THEME = new FixedSelect2BootstrapTheme();
	
	/**
	 * Reimplementation of {@link Select2BootstrapTheme} just to have it {@link Serializable}
	 */
	public static class FixedSelect2BootstrapTheme implements  ISelect2Theme, IClusterable {
		private static final ResourceReference CSS = new CssResourceReference(Select2BootstrapTheme.class, "/res/bootstrap/select2-bootstrap.css");
		public FixedSelect2BootstrapTheme() {
		}

		@Override
		public void renderHead(Component component, IHeaderResponse response) {
			response.render(CssHeaderItem.forReference(CSS));
		}

		@Override
		public String name() {
			return "bootstrap";
		}
	};
	public static final List<String> OCLASS_ATTRS = new ArrayList<String>(OClassPrototyper.OCLASS_ATTRS);
	static
	{
		//Index:OCLASS_ATTRS.indexOf(OClassPrototyper.NAME)+1
		OCLASS_ATTRS.add(2, CustomAttribute.DESCRIPTION.getName());
		OCLASS_ATTRS.add(CustomAttribute.DOMAIN.getName());
		OCLASS_ATTRS.add(CustomAttribute.PROP_NAME.getName());
		OCLASS_ATTRS.add(CustomAttribute.PROP_PARENT.getName());
		OCLASS_ATTRS.add(CustomAttribute.TAB.getName());
        OCLASS_ATTRS.add(CustomAttribute.SORT_BY.getName());
        OCLASS_ATTRS.add(CustomAttribute.SORT_ORDER.getName());
        OCLASS_ATTRS.add(CustomAttribute.SEARCH_QUERY.getName());
		OCLASS_ATTRS.add(CustomAttribute.ON_CREATE_FIELDS.getName());
		OCLASS_ATTRS.add(CustomAttribute.ON_CREATE_IDENTITY_TYPE.getName());
	}
	
	private static final Predicate<OProperty> IS_LINK_PROPERTY = new Predicate<OProperty>() {

		@Override
		public boolean apply(OProperty input) {
			return input!=null?input.getType().isLink():false;
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
		CustomAttribute custom;
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
		else if((CustomAttribute.ON_CREATE_FIELDS.getName().equals(critery)) && (custom = CustomAttribute.getIfExists(critery)) != null)
		{
			String onCreateFields = custom.getValue(entity);
			return (V)(!Strings.isNullOrEmpty(onCreateFields)
					? Lists.newArrayList(onCreateFields.split(","))
					: new ArrayList<String>());
		}
		else if((custom = CustomAttribute.getIfExists(critery))!=null)
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
			CustomAttribute custom;
			if(OClassPrototyper.CLUSTER_SELECTION.equals(critery))
			{
				if(value!=null) entity.setClusterSelection(value.toString());
			}
			else if((CustomAttribute.ON_CREATE_FIELDS.getName().equals(critery)) && (custom = CustomAttribute.getIfExists(critery)) != null)
			{
				custom.setValue(entity, value!=null?Joiner.on(",").join((List<String>) value):null);
			}
			else if((custom = CustomAttribute.getIfExists(critery))!=null)
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
			if(CustomAttribute.match(critery, CustomAttribute.PROP_NAME, CustomAttribute.PROP_PARENT, CustomAttribute.SORT_BY))
			{
				return new OPropertyViewPanel(id, (IModel<OProperty>)getModel());
			}
			else if(OClassPrototyper.SUPER_CLASSES.equals(critery)) {
				return new MultipleOClassesViewPanel(id, (IModel<List<OClass>>)getModel());
			}
			else if(OClassPrototyper.ABSTRACT.equals(critery) || OClassPrototyper.STRICT_MODE.equals(critery))
			{
				return new BooleanViewPanel(id, (IModel<Boolean>)getModel()).setHideIfFalse(true);
			}
			else if(CustomAttribute.match(critery, CustomAttribute.SORT_ORDER))
			{
				return new Label(id, new StringResourceModel("sortorder.${}", getModel()));
			}
			else if(CustomAttribute.match(critery, CustomAttribute.DOMAIN))
			{
				return new Label(id, new EnumNamingModel<OClassDomain>((IModel<OClassDomain>)getModel()));
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
					FormComponent<V> ret = new TextField<V>(id, getModel()).setType(String.class)
									.add((IValidator<V>)OSchemaNamesValidator.CLASS_NAME_VALIDATOR);
					if(OClassPrototyper.NAME.equals(critery)) ret.setRequired(true);
					return ret;
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
					Select2MultiChoice choice = new Select2MultiChoice<OClass>(id, (IModel<Collection<OClass>>)getModel(), OClassTextChoiceProvider.INSTANCE);
					choice.add(new RefreshMetaContextOnChangeBehaviour());
					choice.getSettings().setCloseOnSelect(true).setTheme(BOOTSTRAP_SELECT2_THEME);
					return choice;
				}
				else if(OClassPrototyper.CLUSTER_SELECTION.equals(critery))
				{
					return new DropDownChoice<String>(id, (IModel<String>)getModel(), CLUSTER_SELECTIONS);
				}
				else if(CustomAttribute.match(critery, CustomAttribute.PROP_NAME))
				{
					return new DropDownChoice<OProperty>(id, (IModel<OProperty>)getModel(), new ListOPropertiesModel(getEntityModel(), null)).setNullValid(true);
				}
				else if(CustomAttribute.match(critery, CustomAttribute.PROP_PARENT))
				{
					return new DropDownChoice<OProperty>(id, (IModel<OProperty>)getModel(), new ListOPropertiesModel(getEntityModel(), null) {
						
						@Override
						protected Predicate<? super OProperty> getFilterPredicate() {
							return IS_LINK_PROPERTY;
						}

					}).setNullValid(true);
				}
                else if(CustomAttribute.match(critery, CustomAttribute.SORT_BY))
                {
                    return new DropDownChoice<OProperty>(id, (IModel<OProperty>)getModel(), new ListOPropertiesModel(getEntityModel(), null)).setNullValid(true);
                }
                else if(CustomAttribute.match(critery, CustomAttribute.SORT_ORDER))
                {
                	return new DropDownChoice<Boolean>(id, (IModel<Boolean>)getModel(), Arrays.asList(true, false), new ResourceChoiceRenderer<>("sortorder")).setNullValid(true);
                }
                else if(CustomAttribute.match(critery,CustomAttribute.DESCRIPTION))
                {
                    return new TextArea<V>(id, getModel());
                }
                else if(CustomAttribute.match(critery, CustomAttribute.SEARCH_QUERY))
                {
                    return new TextArea<String>(id, (IModel<String>)getModel())
                    		.add(new PatternValidator("^(select|where)\\s.*", Pattern.CASE_INSENSITIVE));
                }
                else if (CustomAttribute.match(critery,CustomAttribute.TAB))
                {
                    return new TextField<V>(id,getModel());
                }
				else if(CustomAttribute.match(critery, CustomAttribute.ON_CREATE_FIELDS))
				{
					Select2MultiChoice<String> choice = new Select2MultiChoice<String>(id, (IModel<Collection<String>>)getModel(), OnCreateFieldsTextChoiceProvider.INSTANCE);
					choice.getSettings().setCloseOnSelect(true).setTheme(BOOTSTRAP_SELECT2_THEME);
					return choice;
				}
				else if(CustomAttribute.match(critery, CustomAttribute.ON_CREATE_IDENTITY_TYPE))
				{
					return new DropDownChoice<String>(id, (IModel<String>)getModel(), ON_CREATE_IDENTITY_SELECTIONS).setNullValid(true);
				}
				else if(CustomAttribute.match(critery, CustomAttribute.DOMAIN))
				{
					return new DropDownChoice<OClassDomain>(id, (IModel<OClassDomain>)getModel(), Arrays.asList(OClassDomain.values()), new EnumChoiceRenderer<OClassDomain>());
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
			AbstractMetaPanel<OClass, String, ?> onCreateFieldsPanel = getMetaComponent(CustomAttribute.ON_CREATE_FIELDS.getName());
			AbstractMetaPanel<OClass, String, ?> onCreateIdentityTypePanel = getMetaComponent(CustomAttribute.ON_CREATE_IDENTITY_TYPE.getName());
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
