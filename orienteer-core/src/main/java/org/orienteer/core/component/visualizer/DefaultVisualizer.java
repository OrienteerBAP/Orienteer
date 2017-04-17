package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.property.*;
import org.orienteer.core.component.property.filter.BooleanFilterPanel;
import org.orienteer.core.component.property.filter.FilterOPropertyPanel;
import org.orienteer.core.service.IOClassIntrospector;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;

import java.io.Serializable;
import java.util.*;

/**
 * Default {@link IVisualizer}. Should cover all property types
 */
public class DefaultVisualizer extends AbstractSimpleVisualizer
{
	public static final DefaultVisualizer INSTANCE = new DefaultVisualizer();
	
	public DefaultVisualizer()
	{
		super("default", false, OType.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> Component createComponent(String id, DisplayMode mode,
			IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
		return createComponent(id, mode, documentModel, propertyModel, propertyModel.getObject().getType(), valueModel);
	}

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
					return new LinksCollectionViewPanel<OIdentifiable, Collection<OIdentifiable>>(id, documentModel, property);
                case DATE:
                	return new DateLabel(id, (IModel<Date>) valueModel, OrienteerWebApplication.DATE_CONVERTER);
                case DATETIME:
                	return new DateLabel(id, (IModel<Date>) valueModel, OrienteerWebApplication.DATE_TIME_CONVERTER);
                case BOOLEAN:
                	return new BooleanViewPanel(id, (IModel<Boolean>)valueModel);
                case EMBEDDED:
                	return new EmbeddedDocumentPanel(id, (IModel<ODocument>)valueModel, new PropertyModel<OClass>(propertyModel, "linkedClass"), mode.asModel());
                case EMBEDDEDLIST:
                case EMBEDDEDSET:
                	return new EmbeddedCollectionViewPanel<Object, Collection<Object>>(id, documentModel, propertyModel);
                case EMBEDDEDMAP:
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
					return new LinkEditPanel(id, documentModel, propertyModel);
					//return new TextField<V>(id, getModel()).setType(ODocument.class);
				case LINKLIST:
				case LINKSET:
					return new LinksCollectionEditPanel<OIdentifiable, Collection<OIdentifiable>>(id, documentModel, property);
                case DATE:
                    return new DateField(id, (IModel<Date>) valueModel) {
                    	@Override
                    	protected TimeZone getClientTimeZone() {
                    		// We should not convert timezones when working with just dates.
                    		return null;
                    	}
                    };
                case DATETIME:
                    return new DateTimeField(id, (IModel<Date>) valueModel);
                case EMBEDDED:
                	return new EmbeddedDocumentPanel(id, (IModel<ODocument>)valueModel, new PropertyModel<OClass>(propertyModel, "linkedClass"), mode.asModel());
                case EMBEDDEDLIST:
                	return new EmbeddedCollectionEditPanel<Object, List<Object>>(id, documentModel, propertyModel, ArrayList.class);
                case EMBEDDEDSET:
                	return new EmbeddedCollectionEditPanel<Object, Set<Object>>(id, documentModel, propertyModel, HashSet.class);
                case EMBEDDEDMAP:
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
	public <V> Component createFilterComponent(String id, IModel<OProperty> propertyModel, Form form, IModel<V> valueModel) {
		Component component;
		OProperty property = propertyModel.getObject();
		switch (property.getType()) {
			case BOOLEAN:
				component = new BooleanFilterPanel(id, form, (IModel<Boolean>) valueModel);
				break;
			case EMBEDDED:
			case EMBEDDEDLIST:
			case EMBEDDEDMAP:
			case EMBEDDEDSET:
			case LINK:
			case LINKBAG:
			case LINKLIST:
			case LINKMAP:
			case LINKSET:
			case CUSTOM:
			case BINARY:
				component = null;
				break;
			default:
				component = createComponent(id, DisplayMode.EDIT, null, propertyModel, valueModel);
		}

		return component != null ? new FilterOPropertyPanel(id, component) : component;
	}
}
