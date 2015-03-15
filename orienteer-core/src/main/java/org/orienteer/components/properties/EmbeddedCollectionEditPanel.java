package org.orienteer.components.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.components.BootstrapSize;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.commands.AjaxFormCommand;
import org.orienteer.components.properties.visualizers.DefaultVisualizer;
import org.orienteer.model.DynamicPropertyValueModel;
import org.orienteer.services.IMarkupProvider;

import ru.ydn.wicket.wicketorientdb.model.CollectionAdapterModel;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class EmbeddedCollectionEditPanel<T, M extends Collection<T>> extends FormComponentPanel<M>
{
	protected final Class<?> finalType;
	private List<T> data;
	
	@Inject
	private IMarkupProvider markupProvider;
	
	public EmbeddedCollectionEditPanel(String id, final IModel<ODocument> documentModel, final IModel<OProperty> propertyModel, Class<?> finalType)
	{
		super(id, new DynamicPropertyValueModel<M>(documentModel, propertyModel));
		setOutputMarkupId(true);
		this.finalType = finalType;
		final DefaultVisualizer visualizer = DefaultVisualizer.INSTANCE;
		final OType oType = propertyModel.getObject().getLinkedType();
		ListView<T> listView = new ListView<T>("items", new PropertyModel<List<T>>(this, "data")) {

			@Override
			protected void populateItem(final ListItem<T> item) {
				item.add(visualizer.createComponent("item", DisplayMode.EDIT, documentModel, propertyModel, oType, item.getModel()));
				item.add(new AjaxFormCommand<Object>("remove", "command.remove")
						{
							@Override
							public void onClick(AjaxRequestTarget target) {
								getData().remove(item.getIndex());
								target.add(EmbeddedCollectionEditPanel.this);
							}
						}.setBootstrapSize(BootstrapSize.EXTRA_SMALL)
						 .setBootstrapType(BootstrapType.DANGER)
						 .setIcon((String)null));
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
		listView.setReuseItems(true);
		add(listView);
		add(new AjaxFormCommand("add", "command.add")
		{
			@Override
			public void onClick(AjaxRequestTarget target) {
				getData().add(null);
				target.add(EmbeddedCollectionEditPanel.this);
			}
			
		}.setBootstrapSize(BootstrapSize.EXTRA_SMALL)
		 .setBootstrapType(BootstrapType.PRIMARY)
		 .setIcon((String)null));
	}

	public List<T> getData() {
		if(data==null)
		{
			M data = getModelObject();
			this.data = new ArrayList<T>();
			if(data!=null && !data.isEmpty()) this.data.addAll(data);
		}
		return data;
	}
	
	@Override
	protected void onConfigure() {
		//Explicitly prepare data
		getData();
		super.onConfigure();
	}
	
	@Override
	protected void convertInput() {
		M converted;
		List<T> storedData = getData();
		visitFormComponentsPostOrder(this, new IVisitor<FormComponent<Object>, Void>() {

			@Override
			public void component(FormComponent<Object> object,
					IVisit<Void> visit) {
				if(!(EmbeddedCollectionEditPanel.this.equals(object)))
				{
					object.updateModel();
					visit.dontGoDeeper();
				}
			}
		});

		if(finalType.isInstance(storedData)) converted = (M) storedData;
		else
		{
			try
			{
				converted = (M) finalType.newInstance();
				converted.addAll(storedData);
			} catch (InstantiationException e)
			{
				throw new WicketRuntimeException("Can't create instance of class "+finalType.getName(), e);
			} catch (IllegalAccessException e)
			{
				throw new WicketRuntimeException("Can't create instance of class "+finalType.getName(), e);
			}
		}
		setConvertedInput(converted);
	}
	
	@Override
	protected void onModelChanged() {
		data = null;
	}
}
