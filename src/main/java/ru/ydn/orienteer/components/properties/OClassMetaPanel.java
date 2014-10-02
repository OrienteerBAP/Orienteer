package ru.ydn.orienteer.components.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.validation.IValidator;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.orientechnologies.orient.core.collate.OCollate;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OBalancedClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ODefaultClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ORoundRobinClusterSelectionStrategy;
import com.orientechnologies.orient.core.sql.OSQLEngine;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

public class OClassMetaPanel<V> extends AbstractComplexModeMetaPanel<OClass, DisplayMode, String, V>
{
	public static final List<String> OCLASS_ATTRS = new ArrayList<String>(OClassPrototyper.OCLASS_ATTRS);
	static
	{
		OCLASS_ATTRS.add(CustomAttributes.PROP_NAME.getName());
		OCLASS_ATTRS.add(CustomAttributes.PROP_PARENT.getName());
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final List<String> CLUSTER_SELECTIONS = Arrays.asList(new String[]{ODefaultClusterSelectionStrategy.NAME, ORoundRobinClusterSelectionStrategy.NAME, OBalancedClusterSelectionStrategy.NAME});
	
	public static class OClassFieldNameModel extends AbstractNamingModel<String>
	{
		private static final long serialVersionUID = 1L;
		
		public OClassFieldNameModel(IModel<String> objectModel)
		{
			super(objectModel);
		}

		public OClassFieldNameModel(String object)
		{
			super(object);
		}

		@Override
		public String getResourceKey(String object) {
			return "class."+object;
		}
	};
	
	public static class ListClassesModel extends LoadableDetachableModel<List<OClass>>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static final Ordering<OClass> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<OClass, String>() {

			@Override
			public String apply(OClass input) {
				return input.getName();
			}
		});
		@Override
		protected List<OClass> load() {
			Collection<OClass> classes = OrientDbWebSession.get().getDatabase().getMetadata().getSchema().getClasses();
			return ordering.sortedCopy(classes);
		}
		
	}
	
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
		ODatabaseRecord db = OrientDbWebSession.get().getDatabase();
		db.commit();
		try
		{
			CustomAttributes custom;
			if("clusterSelection".equals(critery))
			{
				if(value!=null) entity.setClusterSelection(value.toString());
			}
			else if((custom = CustomAttributes.fromString(critery))!=null)
			{
				custom.setValue(entity, value);
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
		if(DisplayMode.VIEW.equals(mode))
		{
			if(CustomAttributes.match(critery, CustomAttributes.PROP_NAME, CustomAttributes.PROP_PARENT))
			{
				return new OPropertyViewPanel(id, (IModel<OProperty>)getModel());
			}
			else
			{
				return new Label(id, getModel());
			}
		}
		else if(DisplayMode.EDIT.equals(mode))
		{
				if("name".equals(critery) || "shortName".equals(critery))
				{
					return new TextField<V>(id, getModel()).setType(String.class).add((IValidator<V>)OSchemaNamesValidator.INSTANCE);
				}
				else if("abstract".equals(critery) || "strictMode".equals(critery))
				{
					return new BooleanEditPanel(id, (IModel<Boolean>)getModel());
				}
				else if(OClassPrototyper.OVER_SIZE.equals(critery))
				{
					return new TextField<V>(id, getModel()).setType(Float.class);
				}
				else if("superClass".equals(critery))
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
				else if("clusterSelection".equals(critery))
				{
					return new DropDownChoice<String>(id, (IModel<String>)getModel(), CLUSTER_SELECTIONS);
				}
				else if(CustomAttributes.PROP_NAME.getName().equals(critery) 
						|| CustomAttributes.PROP_PARENT.getName().equals(critery))
				{
					return new DropDownChoice<OProperty>(id, (IModel<OProperty>)getModel(), new AbstractReadOnlyModel<List<OProperty>>() {

						@Override
						public List<OProperty> getObject() {
							OClass oClass = getEntityObject();
							if(oClass==null) return null;
							Collection<OProperty> ret = oClass.properties();
							//TODO: filter properties
							return ret instanceof List?(List<OProperty>) ret:new ArrayList<OProperty>(ret);
						}
					});
				}
				else
				{
					return new Label(id, getModel());
				}
		}
		else return null;
	}

	@Override
	public IModel<String> newLabelModel() {
		return new OClassFieldNameModel(getPropertyModel());
	}
	

}
