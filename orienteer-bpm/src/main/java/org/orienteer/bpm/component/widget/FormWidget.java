package org.orienteer.bpm.component.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.orienteer.bpm.camunda.handler.ProcessDefinitionEntityHandler;
import org.orienteer.bpm.camunda.handler.TaskEntityHandler;
import org.orienteer.bpm.component.command.CompleteTaskCommand;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.widget.document.ODocumentPropertiesWidget;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.NvlModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

/**
 * Widget showing a form to a user
 */
@Widget(id="user-task-form", domain="document", selector=TaskEntityHandler.OCLASS_NAME, autoEnable=true, tab="form")
public class FormWidget extends AbstractModeAwareWidget<ODocument> {
	
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	private FormKey formKey;
	private ODocumentModel formDocumentModel;
	private OrienteerStructureTable<ODocument, OProperty> propertiesStructureTable;
	private SaveODocumentCommand saveODocumentCommand;
	
	public FormWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
		TaskService taskService = processEngine.getTaskService();
		Task task = taskService.createTaskQuery()
							.taskId((String)getModelObject().field("id"))
							.initializeFormKeys()
							.singleResult();
		String formKeyStr = task.getFormKey();
		add(new Label("formKey", formKeyStr));
		formKey = FormKey.parse(formKeyStr);
		setVisible(formKey.isValid());
		formDocumentModel = new ODocumentModel(formKey.calculateODocument(processEngine, task.getId()));
		
		Form<ODocument> form = new Form<ODocument>("form", getModel());
		IModel<List<OProperty>> propertiesModel = new LoadableDetachableModel<List<OProperty>>() {
			@Override
			protected List<OProperty> load() {
				return oClassIntrospector.listProperties(formDocumentModel.getObject().getSchemaClass(), IOClassIntrospector.DEFAULT_TAB, false);
			}
		};
		propertiesStructureTable = new OrienteerStructureTable<ODocument, OProperty>("properties", formDocumentModel, propertiesModel){

					@Override
					protected Component getValueComponent(String id,
							IModel<OProperty> rowModel) {
						//TODO: remove static displaymode
						return new ODocumentMetaPanel<Object>(id, getModeModel(), formDocumentModel, rowModel);
					}
		};
		form.add(propertiesStructureTable);
		add(form);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		propertiesStructureTable.addCommand(new EditODocumentCommand(propertiesStructureTable, getModeModel()));
		propertiesStructureTable.addCommand(saveODocumentCommand 
												= new SaveODocumentCommand(propertiesStructureTable, getModeModel()).setForceCommit(true));
		propertiesStructureTable.addCommand(new CompleteTaskCommand(propertiesStructureTable, getModel()) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				if(saveODocumentCommand.determineVisibility()) saveODocumentCommand.onClick(target);
				super.onClick(target);
			}
		});
	}
	

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.tasks);
	}
	
	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new NvlModel<>(new ODocumentPropertyModel<String>(getModel(), "name"), 
							  new ResourceModel("widget.form"));
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		formDocumentModel.detach();
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
