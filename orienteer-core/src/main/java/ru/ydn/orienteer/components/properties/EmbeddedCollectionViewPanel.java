package ru.ydn.orienteer.components.properties;

import java.util.Collection;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.components.properties.visualizers.DefaultVisualizer;
import ru.ydn.orienteer.components.properties.visualizers.IVisualizer;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.CollectionAdapterModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class EmbeddedCollectionViewPanel<T, M extends Collection<T>> extends GenericPanel<M> {

	
	public EmbeddedCollectionViewPanel(String id, final IModel<ODocument> documentModel, final IModel<OProperty> propertyModel) {
		super(id, new DynamicPropertyValueModel<M>(documentModel, propertyModel));
		final DefaultVisualizer visualizer = DefaultVisualizer.INSTANCE;
		final OType oType = propertyModel.getObject().getLinkedType();
		ListView<T> listView = new ListView<T>("items", new CollectionAdapterModel<T, M>(getModel())) {

			@Override
			protected void populateItem(ListItem<T> item) {
				item.add(visualizer.createComponent("item", DisplayMode.VIEW, documentModel, propertyModel, oType, item.getModel()));
			}
		};
		add(listView);
	}

}
