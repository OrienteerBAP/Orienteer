package org.orienteer.core.component.property;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link FormComponentPanel} to edit embedded collections
 *
 * @param <T> the type of collection's objects
 * @param <M> the type of a collection themselves
 */
public class EmbeddedCollectionEditPanel<T, M extends Collection<T>> extends FormComponentPanel<M>
{
	protected final Class<?> finalType;
	private List<T> data;
	private ListView<T> listView;
	private Class<FormComponent> embeddedView;

	@Inject
	private IMarkupProvider markupProvider;
	
	public EmbeddedCollectionEditPanel(String id, final IModel<ODocument> documentModel, final IModel<OProperty> propertyModel, Class<?> finalType)
	{
		super(id, new DynamicPropertyValueModel<M>(documentModel, propertyModel));
		setOutputMarkupId(true);
		this.finalType = finalType;
		final DefaultVisualizer visualizer = DefaultVisualizer.INSTANCE;
		OProperty property = propertyModel.getObject();
		final OType oType = property.getLinkedType()!=null?property.getLinkedType():OType.EMBEDDED;
		listView = new ListView<T>("items", new PropertyModel<List<T>>(this, "data")) {

			@Override
			@SuppressWarnings("unchecked")
			protected void populateItem(final ListItem<T> item) {
				Component component = visualizer.createComponent("item", DisplayMode.EDIT, documentModel, propertyModel, oType, item.getModel());
				if (embeddedView == null && component != null) embeddedView = (Class<FormComponent>) component.getClass();
				item.add(component);
				item.add(new AjaxFormCommand<Object>("remove", "command.remove")
						{
							@Override
							public void onClick(AjaxRequestTarget target) {
								convertToData();
								getData().remove(item.getIndex());
								target.add(EmbeddedCollectionEditPanel.this);
								listView.removeAll();
							}
						}.setDefaultFormProcessing(false)
						 .setAutoNotify(false)
						 .setBootstrapSize(BootstrapSize.EXTRA_SMALL)
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
				convertToData();
				getData().add(null);
				target.add(EmbeddedCollectionEditPanel.this);
				listView.removeAll();
			}
			
		}.setDefaultFormProcessing(false)
		 .setAutoNotify(false)
		 .setBootstrapSize(BootstrapSize.EXTRA_SMALL)
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
	
	protected void convertToData() {
		visitFormComponentsPostOrder(this, new IVisitor<FormComponent<?>, Void>() {

			@Override
			public void component(FormComponent<?> object,
					IVisit<Void> visit) {
				if(embeddedView != null && embeddedView.equals(object.getClass()))
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
		M converted;
		List<T> storedData = getData();

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
	
}
