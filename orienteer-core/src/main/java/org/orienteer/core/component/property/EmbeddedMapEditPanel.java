package org.orienteer.core.component.property;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.command.AjaxFormCommand;
import org.orienteer.core.component.visualizer.DefaultVisualizer;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link FormComponentPanel} to edit embedded {@link Map}
 *
 * @param <V> the type of collection's objects
 */
public class EmbeddedMapEditPanel<V> extends FormComponentPanel<Map<String, V>> {

	protected static class Pair<V> implements Map.Entry<String, V>, Serializable
	{
		private String key;
		private V value;
		
		public Pair()
		{
			
		}
		
		public Pair(Map.Entry<String, V> entry)
		{
			this(entry.getKey(), entry.getValue());
		}
		
		public Pair(String key, V value)
		{
			setKey(key);
			setValue(value);
		}

		@Override
		public String getKey() {
			return key;
		}
		
		public void setKey(String key)
		{
			this.key = key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			this.value = value;
			return null;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "Pair: "+key+"="+value;
		}
		
	}
	private List<Pair<V>> data;
	
	@Inject
	private IMarkupProvider markupProvider;
	
	private ListView<Pair<V>> listView;
	
	public EmbeddedMapEditPanel(String id, final IModel<ODocument> documentModel, final IModel<OProperty> propertyModel)
	{
		super(id, new DynamicPropertyValueModel<Map<String, V>>(documentModel, propertyModel));
		setOutputMarkupId(true);
		OProperty property = propertyModel.getObject();
		final DefaultVisualizer visualizer = DefaultVisualizer.INSTANCE;
		final OType linkedType = property.getLinkedType();
		final OType oType = linkedType != null ? linkedType : (OType.LINKMAP.equals(property.getType()) ? OType.LINK :OType.ANY);
		listView = new ListView<Pair<V>>("items", new PropertyModel<List<Pair<V>>>(this, "data")) {

			@Override
			protected void populateItem(final ListItem<Pair<V>> item) {
				item.add(getKeyEditComponent(item));
				item.add(visualizer.createComponent("item", DisplayMode.EDIT, documentModel, propertyModel, oType, new PropertyModel<V>(item.getModel(), "value")));
				item.add(new AjaxFormCommand<Object>("remove", "command.remove")
						{
							@Override
							public void onClick(AjaxRequestTarget target) {
								convertToData();
								getData().remove(item.getIndex());
								target.add(EmbeddedMapEditPanel.this);
								listView.removeAll();
							}
						}.setDefaultFormProcessing(false)
						 .setAutoNotify(false)
						 .setBootstrapSize(BootstrapSize.EXTRA_SMALL)
						 .setBootstrapType(BootstrapType.DANGER)
						 .setIcon((String)null));
			}
			
			@Override
			protected ListItem<Pair<V>> newItem(int index, IModel<Pair<V>> itemModel) {
				return new ListItem<Pair<V>>(index, itemModel)
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
		listView.setReuseItems(true);
		add(listView);
		add(new AjaxFormCommand("add", "command.add")
		{
			@Override
			public void onClick(AjaxRequestTarget target) {
				convertToData();
				getData().add(new Pair<V>());
				target.add(EmbeddedMapEditPanel.this);
				listView.removeAll();
			}
			
		}.setDefaultFormProcessing(false)
		 .setAutoNotify(false)
		 .setBootstrapSize(BootstrapSize.EXTRA_SMALL)
		 .setBootstrapType(BootstrapType.PRIMARY)
		 .setIcon((String)null));
	}

	protected Component getKeyEditComponent(ListItem<Pair<V>> item) {
		return new TextField<String>("key", new PropertyModel<String>(item.getModel(), "key"), String.class)
				.setConvertEmptyInputStringToNull(false);
	}

	public List<Pair<V>> getData() {
		if(data==null)
		{
			this.data = new ArrayList<Pair<V>>();
			Map<String, V> data = getConvertedInput();
			if(data==null) data = getModelObject();
			if(data!=null)
			{
				for(Map.Entry<String, V> entry : data.entrySet())
				{
					this.data.add(new Pair<V>(entry));
				}
			}
		}
		return data;
	}
	
	@Override
	protected void onConfigure() {
		//Explicitly prepare data
		getData();
		super.onConfigure();
	}
	
	protected void convertToData() {
		visitChildren(FormComponent.class, new IVisitor<FormComponent<Object>, Void>() {

			@Override
			public void component(FormComponent<Object> object,
					IVisit<Void> visit) {
				if(!(EmbeddedMapEditPanel.this.equals(object)))
				{
					object.convertInput();
					object.updateModel();
					visit.dontGoDeeper();
				}
			}
		});
	}
	
	@Override
	public void convertInput() {
		convertToData();

		Map<String, V> converted = new HashMap<String, V>();
		for(Pair<V> pair: getData())
		{
			converted.put(pair.getKey(), pair.getValue());
		}
		setConvertedInput(converted);
	}
	
}
