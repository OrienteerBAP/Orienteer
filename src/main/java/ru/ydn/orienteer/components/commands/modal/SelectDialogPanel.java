package ru.ydn.orienteer.components.commands.modal;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.TabsPanel;
import ru.ydn.orienteer.components.commands.AbstractCheckBoxEnabledCommand;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.services.IOClassIntrospector;
import ru.ydn.orienteer.web.SearchPage;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class SelectDialogPanel extends GenericPanel<String>
{
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	private ModalWindow modal;
	private boolean canChangeClass;
	private WebMarkupContainer resultsContainer;
	private IModel<OClass> selectedClassModel;
	
	public SelectDialogPanel(String id, final ModalWindow modal, IModel<OClass> initialClass)
	{
		this(id, modal, initialClass.getObject(), initialClass.getObject()==null);
	}
	
	public SelectDialogPanel(String id, final ModalWindow modal, OClass initialClass, boolean canChangeClass)
	{
		super(id, Model.of(""));
		this.modal = modal;
		this.canChangeClass = canChangeClass || initialClass==null;
		this.selectedClassModel = new OClassModel(initialClass!=null?initialClass: getClasses().get(0));
		
		Form<String> form = new Form<String>("form", getModel());
		form.add(new TextField<String>("query", getModel()));
		form.add(new AjaxButton("search") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);
				target.add(resultsContainer);
			}
		});
		
		form.add(new TabsPanel<OClass>("tabs", selectedClassModel, new PropertyModel<List<OClass>>(this, "classes"))
				{

					@Override
					public void onTabClick(AjaxRequestTarget target) {
						prepareResults();
						target.add(resultsContainer);
					}
			
				}.setVisible(canChangeClass));
		
		resultsContainer = new WebMarkupContainer("resultsContainer")
		{
			{
				setOutputMarkupPlaceholderTag(true);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!Strings.isEmpty(SelectDialogPanel.this.getModelObject()));
			}
			
		};
		
		prepareResults();
		form.add(resultsContainer);
		add(form);
	}
	
	public List<OClass> getClasses()
	{
		return SearchPage.CLASSES_ORDERING.sortedCopy(OrientDbWebSession.get().getDatabase().getMetadata().getSchema().getClasses());
	}
	
	private void prepareResults()
	{
		prepareResults(selectedClassModel.getObject());
	}
	
	private void prepareResults(OClass oClass)
	{
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>("select from "+oClass.getName()+" where any() containstext :text");
		provider.setParameter("text", getModel());
		OrienteerDataTable<ODocument, String> table = new OrienteerDataTable<ODocument, String>("results", oClassIntrospector.getColumnsFor(oClass, true), provider, 20);
		table.addCommand(new AbstractCheckBoxEnabledCommand<ODocument>(new ResourceModel("command.select"), table)
				{
					
					{
						setBootstrapType(BootstrapType.SUCCESS);
						setIcon(FAIconType.hand_o_right);
					}

					@Override
					protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
						if(onSelect(target, objects)) modal.close(target);
					}
					
				});
		resultsContainer.addOrReplace(table);
	}
	
	protected abstract boolean onSelect(AjaxRequestTarget target, List<ODocument> objects);
}
