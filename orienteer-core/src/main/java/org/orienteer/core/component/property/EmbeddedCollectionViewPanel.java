package org.orienteer.core.component.property;

import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.DefaultVisualizer;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.service.IMarkupProvider;

import ru.ydn.wicket.wicketorientdb.model.CollectionAdapterModel;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link FormComponentPanel} to view embedded collections
 *
 * @param <T> the type of collection's objects
 * @param <M> the type of a collection themselves
 */
public class EmbeddedCollectionViewPanel<T, M extends Collection<T>> extends GenericPanel<M> {

	@Inject
	private IMarkupProvider markupProvider;
	
	public EmbeddedCollectionViewPanel(String id, final IModel<ODocument> documentModel, final IModel<OProperty> propertyModel) {
		super(id, new DynamicPropertyValueModel<M>(documentModel, propertyModel));
		final DefaultVisualizer visualizer = DefaultVisualizer.INSTANCE;
		OProperty property = propertyModel.getObject();
		final OType oType = property.getLinkedType()!=null?property.getLinkedType():OType.EMBEDDED;
		ListView<T> listView = new ListView<T>("items", new CollectionAdapterModel<T, M>(getModel())) {

			@Override
			protected void populateItem(ListItem<T> item) {
				item.add(visualizer.createComponent("item", DisplayMode.VIEW, documentModel, propertyModel, oType, item.getModel()));
			}
			
			@Override
			protected ListItem<T> newItem(int index, IModel<T> itemModel) {
				return new ListItem<T>(index, itemModel)
						{
							@Override
							public IMarkupFragment getMarkup(Component child) {
								if(child==null || !child.getId().equals("item")) return super.getMarkup(child);
								IMarkupFragment ret = markupProvider.provideMarkup(child);
								return ret!=null?ret:super.getMarkup(child);
							}
						};
			}
		};
		add(listView);
	}

}
