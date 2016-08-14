package org.orienteer.bpm.component.command;

import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.orienteer.bpm.component.widget.FormKey;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxFormCommand;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.web.ODocumentPage;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Command to complete a task 
 */
public class CompleteTaskCommand extends AjaxFormCommand<ODocument>{
	
	private final IModel<ODocument> taskModel;
	
	public CompleteTaskCommand(OrienteerStructureTable<ODocument, ?> component, IModel<ODocument> taskModel) {
		super(new ResourceModel("command.complete"), component);
		this.taskModel = taskModel;
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		setIcon(FAIconType.bolt);
		setBootstrapType(BootstrapType.PRIMARY);
		setChandingModel(true);
	}
	
	@Override
	public void onClick(AjaxRequestTarget target) {
		ODocument doc = getModelObject();
		ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
		TaskService taskService = processEngine.getTaskService();
		String taskId = taskModel.getObject().field("id");
		taskService.complete(taskId);
		setResponsePage(new ODocumentPage(doc));
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		taskModel.detach();
	}
	
	
}
