package org.orienteer.bpm.component.widget;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.orienteer.bpm.camunda.handler.TaskEntityHandler;
import org.orienteer.bpm.component.command.CompleteTaskCommand;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.NvlModel;

/**
 * Widget showing a form for a user task
 */
@Widget(id="user-task-form", domain="document", selector=TaskEntityHandler.OCLASS_NAME, autoEnable=true, tab="form")
public class TaskFormWidget extends AbstractFormWidget {
	
	private SaveODocumentCommand saveODocumentCommand;
	
	public TaskFormWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		propertiesStructureTable.addCommand(new EditODocumentCommand(propertiesStructureTable, getModeModel()));
		propertiesStructureTable.addCommand(saveODocumentCommand 
												= new SaveODocumentCommand(propertiesStructureTable, getModeModel()){
			@Override
			public void onClick(Optional<AjaxRequestTarget> targetOptional) {
				super.onClick(targetOptional);
				associateTaskWithDocument();
			};
		}.setForceCommit(true));
		propertiesStructureTable.addCommand(new CompleteTaskCommand(propertiesStructureTable, getModel(), getModeModel(), formKey));
	}
	
	@Override
	protected FormKey obtainFormKey() {
		               ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
		               TaskService taskService = processEngine.getTaskService();
		               Task task = taskService.createTaskQuery()
                                           .taskId((String)getModelObject().field("id"))
                                           .initializeFormKeys()
                                           .singleResult();
		               return FormKey.parse(task.getFormKey());
	}
	
	@Override
	protected ODocument resolveODocument(FormKey formKey) {
		return formKey.calculateODocument(BpmPlatform.getDefaultProcessEngine(), (String)getModelObject().field("id"));
	}
	
	protected void associateTaskWithDocument() {
		associateTaskWithDocument((String)getModelObject().field("id"), formDocumentModel.getObject());
	}
	
	protected void associateTaskWithDocument(String taskId, ODocument doc) {
		ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
		TaskService taskService = processEngine.getTaskService();
		String var = formKey.getVariableName();
		taskService.setVariable(taskId, var, doc.getIdentity().toString());
	}
	
	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new NvlModel<>(new PropertyModel<String>(getModel(), "name"), 
							  new ResourceModel("widget.form"));
	}

}
