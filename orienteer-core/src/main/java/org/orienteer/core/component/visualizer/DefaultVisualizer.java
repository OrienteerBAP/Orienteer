package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.CollectionModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.filter.AbstractFilterPanel;
import org.orienteer.core.component.filter.FilterPanel;
import org.orienteer.core.component.property.*;
import org.orienteer.core.component.property.date.ODateField;
import org.orienteer.core.component.property.date.ODateLabel;
import org.orienteer.core.component.property.date.ODateTimeField;
import org.orienteer.core.component.property.filter.*;
import org.orienteer.core.service.IOClassIntrospector;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaManager;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.io.Serializable;
import java.util.*;

/**
 * Default {@link IVisualizer}. Should cover all property types
 */
public class DefaultVisualizer extends AbstractSimpleVisualizer {
	public static final String NAME = "default";
	public static final DefaultVisualizer INSTANCE = new DefaultVisualizer();

	public DefaultVisualizer()
	{
		super(NAME, false, OType.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> Component createComponent(String id, DisplayMode mode,
			IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
		return createComponent(id, mode, documentModel, propertyModel, propertyModel.getObject().getType(), valueModel);
	}

	@SuppressWarnings("unchecked")
	public <V> Component createComponent(String id, DisplayMode mode,
			final IModel<ODocument> documentModel,final  IModel<OProperty> propertyModel, OType oType, IModel<V> valueModel) {
		OProperty property = propertyModel.getObject();
		if(DisplayMode.VIEW.equals(mode))
		{
			switch(oType)
			{
				case LINK:
					return new LinkViewPanel(id, (IModel<ODocument>)valueModel);
				case LINKLIST:
				case LINKSET:
					return new LinksCollectionViewPanel<>(id, documentModel, property);
                case DATE:
                	return new ODateLabel(id, (IModel<Date>) valueModel, false, false);
                case DATETIME:
                	return new ODateLabel(id, (IModel<Date>) valueModel, true);
                case BOOLEAN:
                	return new BooleanViewPanel(id, (IModel<Boolean>)valueModel);
                case EMBEDDED:
                	return new EmbeddedDocumentPanel(id, (IModel<ODocument>)valueModel, new PropertyModel<OClass>(propertyModel, "linkedClass"), mode.asModel());
                case EMBEDDEDLIST:
                case EMBEDDEDSET:
                	return new EmbeddedCollectionViewPanel<>(id, documentModel, propertyModel);
                case EMBEDDEDMAP:
                case LINKMAP:
                	return new EmbeddedMapViewPanel<V>(id, documentModel, propertyModel);
                case BINARY:
                	return new BinaryViewPanel(id, documentModel, propertyModel, valueModel);
                default:
					return new Label(id, valueModel);
			}
		}
		else if(DisplayMode.EDIT.equals(mode))
		{
			switch(oType)
			{
				case BOOLEAN:
					return new CheckBox(id, (IModel<Boolean>)valueModel);
				case LINK:
					return new LinkEditPanel(id, documentModel, propertyModel, (IModel<OIdentifiable>)valueModel);
					//return new TextField<V>(id, getModel()).setType(ODocument.class);
				case LINKLIST:
				case LINKSET:
					return new LinksCollectionEditPanel<>(id, documentModel, property);
                case DATE:
					return new ODateField(id, (IModel<Date>) valueModel);
                case DATETIME:
                    return new ODateTimeField(id, (IModel<Date>) valueModel);
                case EMBEDDED:
                	return new EmbeddedDocumentPanel(id, (IModel<ODocument>)valueModel, new PropertyModel<OClass>(propertyModel, "linkedClass"), mode.asModel());
                case EMBEDDEDLIST:
                	return new EmbeddedCollectionEditPanel<Object, List<Object>>(id, documentModel, propertyModel, ArrayList.class);
                case EMBEDDEDSET:
                	return new EmbeddedCollectionEditPanel<Object, Set<Object>>(id, documentModel, propertyModel, HashSet.class);
                case EMBEDDEDMAP:
                case LINKMAP:
                	return new EmbeddedMapEditPanel<V>(id, documentModel, propertyModel);
                case BINARY:
                	return new BinaryEditPanel(id, documentModel, propertyModel, (IModel<byte[]>)valueModel);
                default:
                	TextField<V> ret = new TextField<V>(id, valueModel);
                	Class<?> javaOType = oType.getDefaultJavaType();
                	if(javaOType!=null) ret.setType(javaOType);
                	return ret;
			}
		}
		else return null;
	}

	public <V extends Serializable> Component createNonSchemaFieldComponent(String id, DisplayMode mode, IModel<ODocument> documentModel,
																			String field, Object value, OType oType) {
		IOClassIntrospector introspector = OrienteerWebApplication.get().getServiceInstance(IOClassIntrospector.class);
		ODocument doc = documentModel.getObject();
		OProperty virtualizedProperty = introspector.virtualizeField(doc, field);
		IModel<OProperty> propertyModel = new OPropertyModel(virtualizedProperty);
		IModel<V> valueModel = new DynamicPropertyValueModel<V>(documentModel, propertyModel);
		return createComponent(id, mode, documentModel, propertyModel, oType, valueModel);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> Component getFilterComponent(final String id, final IModel<OProperty> propertyModel,
											final FilterForm<OQueryModel<?>> filterForm) {
		IFilterCriteriaManager manager = getManager(propertyModel, filterForm);
		OProperty property = propertyModel.getObject();
		OType type = property.getType();
		List<AbstractFilterPanel<?, ?>> filters = new LinkedList<>();

		switch (type) {
			case LINKBAG:
			case TRANSIENT:
			case BINARY:
			case ANY:
				return null;
			case EMBEDDED:
				filters.add(new EmbeddedContainsValuePanel(FilterPanel.PANEL_ID, Model.of(),
						id, propertyModel, DefaultVisualizer.this, manager));
				filters.add(new EmbeddedContainsKeyPanel(FilterPanel.PANEL_ID, Model.of(),
						id, propertyModel, DefaultVisualizer.this, manager));
				break;
			case LINK:
				filters.add(new LinkEqualsFilterPanel(FilterPanel.PANEL_ID, Model.of(),
						id, propertyModel, DefaultVisualizer.this, manager));
				filters.add(new CollectionLinkFilterPanel(FilterPanel.PANEL_ID, new CollectionModel<>(),
						id, propertyModel, DefaultVisualizer.this, manager));
				break;
			case EMBEDDEDLIST:
			case EMBEDDEDSET:
				OProperty prop = propertyModel.getObject();
				if (prop != null) {
					if (prop.getLinkedType() != null) {
						filters.add(new EmbeddedCollectionContainsFilterPanel(FilterPanel.PANEL_ID, Model.of(),
								id, propertyModel, DefaultVisualizer.this, manager));
					} else {
						filters.add(new EmbeddedCollectionFilterPanel(FilterPanel.PANEL_ID, new CollectionModel<>(),
								id, propertyModel, DefaultVisualizer.this, manager, true));
					}
				}
				break;
			case LINKLIST:
			case LINKSET:
				filters.add(new CollectionLinkFilterPanel(FilterPanel.PANEL_ID, new CollectionModel<>(),
						id, propertyModel, DefaultVisualizer.this, manager));
				break;
			case EMBEDDEDMAP:
			case LINKMAP:
				filters.add(new MapContainsKeyFilterPanel(FilterPanel.PANEL_ID, Model.<String>of(),
						id, propertyModel, DefaultVisualizer.this, manager));
				if (type == OType.EMBEDDEDMAP) {
					filters.add(new EmbeddedMapContainsValueFilterPanel(FilterPanel.PANEL_ID, Model.of(),
							id, propertyModel, DefaultVisualizer.this, manager));
				} else {
					filters.add(new LinkMapContainsValueFilterPanel(FilterPanel.PANEL_ID, Model.<ODocument>of(),
							id, propertyModel, DefaultVisualizer.this, manager));
				}
				break;
			case STRING:

				filters.add(new ContainsStringFilterPanel(FilterPanel.PANEL_ID, Model.<String>of(),
						id, propertyModel, DefaultVisualizer.this, manager));
				filters.add(new EqualsFilterPanel(FilterPanel.PANEL_ID, Model.<String>of(),
						id, propertyModel, DefaultVisualizer.this, manager));
				filters.add(new CollectionFilterPanel(FilterPanel.PANEL_ID, new CollectionModel<String>(),
						id, propertyModel, DefaultVisualizer.this, manager));
				break;
			case BOOLEAN:
				filters.add(new EqualsFilterPanel(FilterPanel.PANEL_ID, Model.<Boolean>of(),
						id, propertyModel, DefaultVisualizer.this, manager));
				break;
			default:
				filters.add(new EqualsFilterPanel(FilterPanel.PANEL_ID, Model.of(),
						id, propertyModel, DefaultVisualizer.this, manager));
				filters.add(new CollectionFilterPanel(FilterPanel.PANEL_ID, new CollectionModel<>(),
						id, propertyModel, DefaultVisualizer.this, manager));
				filters.add(new RangeFilterPanel(FilterPanel.PANEL_ID, new CollectionModel<>(),
						id, propertyModel, DefaultVisualizer.this, manager));
		}

		return new FilterPanel(id, new OPropertyNamingModel(propertyModel), filterForm, filters);
	}


	private IFilterCriteriaManager getManager(IModel<OProperty> propertyModel, FilterForm<OQueryModel<?>> filterForm) {
		OQueryModel<?> queryModel = filterForm.getStateLocator().getFilterState();
		IFilterCriteriaManager criteriaManager = queryModel.getFilterCriteriaManager(propertyModel.getObject().getName());
		if (criteriaManager == null) {
			criteriaManager = new FilterCriteriaManager(propertyModel);
			queryModel.addFilterCriteriaManager(propertyModel.getObject().getName(), criteriaManager);
		}
		return criteriaManager;
	}
}
