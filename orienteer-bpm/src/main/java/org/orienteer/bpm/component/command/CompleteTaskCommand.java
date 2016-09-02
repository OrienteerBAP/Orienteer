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
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.web.ODocumentPage;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Command to complete a task 
 */
public class CompleteTaskCommand extends SaveODocumentCommand{
	
	private final IModel<ODocument> taskModel;
	private final FormKey formKey;
	
	public CompleteTaskCommand(OrienteerStructureTable<ODocument, ?> component, IModel<ODocument> taskModel, IModel<DisplayMode> displayModeModel, FormKey formKey) {
		super(component, displayModeModel);
		this.taskModel = taskModel;
		this.formKey = formKey;
		setLabelModel(new ResourceModel("command.complete"));
		setAutoNotify(false);
		setForceCommit(true);
		setIcon(FAIconType.bolt);
		setBootstrapType(BootstrapType.SUCCESS);
		setChandingModel(true);
	}
	
	@Override
	public void onClick(AjaxRequestTarget target) {
		super.onClick(target);
		ODocument doc = getModelObject();
		ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
		TaskService taskService = processEngine.getTaskService();
		String taskId = taskModel.getObject().field("id");
		String var = formKey.getVariableName();
		taskService.complete(taskId, CommonUtils.<String, Object>toMap(var, doc.getIdentity().toString()));
		setResponsePage(new ODocumentPage(doc));
		sendActionPerformed();
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getModelObject().getIdentity().isPersistent() || getModeObject().canModify());
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		taskModel.detach();
	}
	
	
}
