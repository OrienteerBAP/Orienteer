package org.orienteer.core.component.visualizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.EmbeddedCollectionEditPanel;
import org.orienteer.core.component.property.EmbeddedCollectionViewPanel;
import org.orienteer.core.component.property.EmbeddedDocumentPanel;
import org.orienteer.core.component.property.EmbeddedMapEditPanel;
import org.orienteer.core.component.property.EmbeddedMapViewPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IVisualizer} to show LINKXXX properties as embedded 
 */
public class LinksAsEmbeddedVisualizer extends AbstractSimpleVisualizer{

	private static final String NAME = "embedded";
	
	public LinksAsEmbeddedVisualizer() {
		super(NAME, false, OType.LINK, OType.LINKLIST, OType.LINKBAG, OType.LINKSET, OType.LINKMAP);
	}

	@Override
	public <V> Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel,
			IModel<OProperty> propertyModel, IModel<V> valueModel) {
		OProperty property = propertyModel.getObject();
		OType oType = property.getType();
		if(DisplayMode.VIEW.equals(mode))
		{
			switch(oType)
			{
				case LINK:
					return new EmbeddedDocumentPanel(id, (IModel<ODocument>)valueModel, new PropertyModel<OClass>(propertyModel, "linkedClass"), mode.asModel());
				case LINKLIST:
				case LINKSET:
					return new EmbeddedCollectionViewPanel<>(id, documentModel, propertyModel);
                case LINKMAP:
                	return new EmbeddedMapViewPanel<V>(id, documentModel, propertyModel);
                default:
                	return null;
			}
		}
		else if(DisplayMode.EDIT.equals(mode))
		{
			switch(oType)
			{
				case LINK:
					return new EmbeddedDocumentPanel(id, (IModel<ODocument>)valueModel, new PropertyModel<OClass>(propertyModel, "linkedClass"), mode.asModel());
				case LINKLIST:
					return new EmbeddedCollectionEditPanel<Object, List<Object>>(id, documentModel, propertyModel, ArrayList.class);
				case LINKSET:
					return new EmbeddedCollectionEditPanel<Object, Set<Object>>(id, documentModel, propertyModel, HashSet.class);
                case LINKMAP:
                	return new EmbeddedMapEditPanel<V>(id, documentModel, propertyModel);
                default:
                	return null;
			}
		}
		else return null;
	}

}
