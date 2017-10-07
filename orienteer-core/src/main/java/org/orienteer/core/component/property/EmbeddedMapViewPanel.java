package org.orienteer.core.component.property;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.orienteer.core.component.visualizer.DefaultVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.model.CollectionAdapterModel;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;

import java.util.Map;
import java.util.Set;

/**
 * {@link FormComponentPanel} to view embedded {@link Map}
 *
 * @param <V> the type of collection's objects
 */
public class EmbeddedMapViewPanel<V> extends GenericPanel<Map<String, V>> {
	
	@Inject
	private IMarkupProvider markupProvider;
	
	public EmbeddedMapViewPanel(String id, final IModel<ODocument> documentModel, final IModel<OProperty> propertyModel) {
		super(id, new DynamicPropertyValueModel<Map<String, V>>(documentModel, propertyModel));
		OProperty property = propertyModel.getObject();
		final DefaultVisualizer visualizer = DefaultVisualizer.INSTANCE;
		final OType linkedType = property.getLinkedType();
		final OType oType = linkedType != null ? linkedType : (OType.LINKMAP.equals(property.getType()) ? OType.LINK :OType.ANY);
		IModel<Set<Map.Entry<String, V>>> entriesModel = new PropertyModel<>(getModel(), "entrySet()");
		ListView<Map.Entry<String, V>> listView = 
				new ListView<Map.Entry<String, V>>("items", new CollectionAdapterModel<>(entriesModel)) {

			@Override
			protected void populateItem(ListItem<Map.Entry<String, V>> item) {
				item.add(new Label("key", new PropertyModel<String>(item.getModel(), "key")));
				item.add(visualizer.createComponent("item", DisplayMode.VIEW, documentModel, propertyModel, oType, new PropertyModel<V>(item.getModel(), "value")));
			}
			
			@Override
			protected ListItem<Map.Entry<String, V>> newItem(int index, IModel<Map.Entry<String, V>> itemModel) {
				return new ListItem<Map.Entry<String, V>>(index, itemModel)
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
