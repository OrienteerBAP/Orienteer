package ru.ydn.orienteer.components.properties;

import java.io.Serializable;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.validation.IValidator;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OBalancedClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ODefaultClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ORoundRobinClusterSelectionStrategy;

import ru.ydn.orienteer.components.IMetaComponentResolver;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

public class OClassMetaPanel<V> extends AbstractComplexMapMetaPanel<OClass, DisplayMode, String, V>
{
	private static final List<String> CLUSTER_SELECTIONS = Arrays.asList(new String[]{ODefaultClusterSelectionStrategy.NAME, ORoundRobinClusterSelectionStrategy.NAME, OBalancedClusterSelectionStrategy.NAME});
	public static class ListClassesModel extends LoadableDetachableModel<List<OClass>>
	{
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
		if("clusterSelection".equals(critery))
		{
			return (V)entity.getClusterSelection().getName();
		}
		else
		{
			return (V)PropertyResolver.getValue(critery, entity);
		}
	}

	@Override
	protected void setValue(OClass entity, String critery, V value) {
		if("clusterSelection".equals(critery))
		{
			if(value!=null) entity.setClusterSelection(value.toString());
		}
		else
		{
			PropertyResolver.setValue(critery, entity, value, new PropertyResolverConverter(Application.get().getConverterLocator(),
					Session.get().getLocale()));
		}
	}





	@Override
	protected IMetaComponentResolver<String> newResolver(DisplayMode key) {
		if(DisplayMode.VIEW.equals(key))
		{
			return new IMetaComponentResolver<String>() {

				@Override
				public Component resolve(String id, String critery) {
					return new Label(id, getModel());
				}

				@Override
				public Serializable getSignature(String critery) {
					return critery;
				}
			};
		}
		else if(DisplayMode.EDIT.equals(key))
		{
			return new IMetaComponentResolver<String>() {

				@SuppressWarnings("unchecked")
				@Override
				public Component resolve(String id, String critery) {
					if("name".equals(critery) || "shortName".equals(critery))
					{
						return new TextFieldEditPanel<V>(id, getModel()).addValidator((IValidator<V>)OSchemaNamesValidator.INSTANCE).setType(String.class);
					}
					else if("abstract".equals(critery) || "strictMode".equals(critery))
					{
						return new BooleanEditPanel(id, (IModel<Boolean>)getModel());
					}
					else if("superClass".equals(critery))
					{
						return new DropDownChoice<OClass>(id, (IModel<OClass>)getModel(), new ListClassesModel(), new IChoiceRenderer<OClass>() {

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
					else
					{
						return new Label(id, getModel());
					}
				}

				@Override
				public Serializable getSignature(String critery) {
					return critery;
				}
			};
		}
		else return null;
	}
	

}
