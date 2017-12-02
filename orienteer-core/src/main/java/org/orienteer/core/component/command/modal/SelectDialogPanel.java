package org.orienteer.core.component.command.modal;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
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
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.TabsPanel;
import org.orienteer.core.component.command.AbstractCheckBoxEnabledCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.web.SearchPage;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.List;

/**
 * Modal window for selecting an {@link ODocument}
 */
public abstract class SelectDialogPanel extends GenericPanel<String>
{
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	protected ModalWindow modal;
	private boolean isMultiValue;
	private boolean canChangeClass;
	private WebMarkupContainer resultsContainer;
	private IModel<OClass> selectedClassModel;
	private TextField<String> queryField;

	public SelectDialogPanel(String id, final ModalWindow modal, IModel<OClass> initialClass, boolean isMultiValue)
	{
		this(id, modal, initialClass.getObject(), initialClass.getObject()==null, isMultiValue);
	}
	
	public SelectDialogPanel(String id, final ModalWindow modal, OClass initialClass, boolean canChangeClass, boolean isMultiValue)
	{
		super(id, Model.of(""));
		this.modal = modal;
		this.isMultiValue = isMultiValue;
		this.modal.setMinimalHeight(400);
		this.canChangeClass = canChangeClass || initialClass==null;
		this.selectedClassModel = new OClassModel(initialClass!=null?initialClass: getClasses().get(0));
		
		Form<String> form = new Form<String>("form", getModel());
		queryField = new TextField<String>("query", getModel(), String.class);
		queryField.setOutputMarkupId(true);
		form.add(queryField);
		AjaxButton searchButton = new AjaxButton("search") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);
				target.add(resultsContainer);
			}
		}; 
		form.add(searchButton);
		form.setDefaultButton(searchButton);
		
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
		OQueryDataProvider<ODocument> provider = oClassIntrospector.getDataProviderForGenericSearch(oClass, getModel());
		oClassIntrospector.defineDefaultSorting(provider, oClass);
		GenericTablePanel<ODocument> tablePanel
				= new GenericTablePanel<ODocument>("results", oClassIntrospector.getColumnsFor(oClass, true, DisplayMode.VIEW.asModel()), provider, 20);
		OrienteerDataTable<ODocument, String> table = tablePanel.getDataTable();
		table.addCommand(new AbstractCheckBoxEnabledCommand<ODocument>(new ResourceModel("command.select"), table) {
					
			{
				setBootstrapType(BootstrapType.SUCCESS);
				setIcon(FAIconType.hand_o_right);
				setAutoNotify(false);
			}

			@Override
			protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
				if(onSelect(target, objects, false)) modal.close(target);
			}

		});

		if (isMultiValue) {
			table.addCommand(new AbstractCheckBoxEnabledCommand<ODocument>(new ResourceModel("command.selectAndSearchMode"), table) {

				{
					setBootstrapType(BootstrapType.SUCCESS);
					setIcon(FAIconType.hand_o_right);
					setAutoNotify(false);
				}

				@Override
				protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
					if (onSelect(target, objects, true)) {
						resetSelection();
						target.add(getTable());
						target.focusComponent(queryField);
						target.appendJavaScript("$('#"+queryField.getMarkupId()+"').select()");
					}
				}

			});
		}
		resultsContainer.addOrReplace(tablePanel);
	}

	protected abstract boolean onSelect(AjaxRequestTarget target, List<ODocument> objects, boolean selectMore);
}
