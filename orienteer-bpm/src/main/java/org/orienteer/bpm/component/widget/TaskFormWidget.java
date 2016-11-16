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
			public void onClick(AjaxRequestTarget target) {
				super.onClick(target);
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
		return new NvlModel<>(new ODocumentPropertyModel<String>(getModel(), "name"), 
							  new ResourceModel("widget.form"));
	}

}
