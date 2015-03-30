package org.orienteer.components.properties.visualizers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.orienteer.components.properties.BinaryEditPanel;
import org.orienteer.components.properties.BinaryViewPanel;
import org.orienteer.components.properties.BooleanViewPanel;
import org.orienteer.components.properties.DisplayMode;
import org.orienteer.components.properties.EmbeddedCollectionEditPanel;
import org.orienteer.components.properties.EmbeddedCollectionViewPanel;
import org.orienteer.components.properties.EmbeddedMapViewPanel;
import org.orienteer.components.properties.LinkEditPanel;
import org.orienteer.components.properties.LinkViewPanel;
import org.orienteer.components.properties.LinksCollectionEditPanel;
import org.orienteer.components.properties.LinksCollectionViewPanel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

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
			IModel<ODocument> documentModel, IModel<OProperty> propertyModel, OType oType, IModel<V> valueModel) {
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
                    return DateLabel.forDatePattern(id, (IModel<Date>) valueModel, ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Session.get().getLocale())).toPattern());
                case DATETIME:
                    return DateLabel.forDatePattern(id, (IModel<Date>) valueModel, ((SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG, Session.get().getLocale())).toPattern());
                case BOOLEAN:
                	return new BooleanViewPanel(id, (IModel<Boolean>)valueModel);
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
                    return new DateField(id, (IModel<Date>) valueModel);
                case DATETIME:
                    return new DateTimeField(id, (IModel<Date>) valueModel);
                case EMBEDDEDLIST:
                	return new EmbeddedCollectionEditPanel<Object, List<Object>>(id, documentModel, propertyModel, ArrayList.class);
                case EMBEDDEDSET:
                	return new EmbeddedCollectionEditPanel<Object, Set<Object>>(id, documentModel, propertyModel, HashSet.class);
                case BINARY:
                	return new BinaryEditPanel(id, (IModel<byte[]>)valueModel);
                default:
                	TextField<V> ret = new TextField<V>(id, valueModel);
                	Class<?> javaOType = oType.getDefaultJavaType();
                	if(javaOType!=null) ret.setType(javaOType);
                	return ret;
			}
		}
		else return null;
	}

}
