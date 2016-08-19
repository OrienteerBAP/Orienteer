package org.orienteer.bpm.component.widget;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.orienteer.bpm.camunda.handler.ProcessDefinitionEntityHandler;
import org.orienteer.bpm.camunda.handler.TaskEntityHandler;
import org.orienteer.bpm.component.command.CompleteTaskCommand;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Form to be shown to start a new process 
 */
@Widget(id="process-definition-form", domain="document", selector=ProcessDefinitionEntityHandler.OCLASS_NAME, autoEnable=true, tab="form")
public class ProcessDefinitionFormWidget extends AbstractFormWidget {
	

	public ProcessDefinitionFormWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		propertiesStructureTable.addCommand(new EditODocumentCommand(propertiesStructureTable, getModeModel()));
		propertiesStructureTable.addCommand(new SaveODocumentCommand(propertiesStructureTable, getModeModel()){
			
			protected void onInitialize() {
				super.onInitialize();
				setLabelModel(new ResourceModel("command.saveAndStart"));
			};
			
			public void onClick(AjaxRequestTarget target) {
				super.onClick(target);
				ODocument doc = formDocumentModel.getObject();
				Map<String, Object> variables = new HashMap<>();
				variables.put(formKey.getVariableName(), doc.getIdentity().toString());
				BpmPlatform.getDefaultProcessEngine().getRuntimeService()
					.startProcessInstanceById((String)ProcessDefinitionFormWidget.this.getModelObject().field("id"), variables);
				setResponsePage(new ODocumentPage(doc));
			};
		}.setForceCommit(true).setBootstrapType(BootstrapType.SUCCESS));
	}

	@Override
	protected FormKey obtainFormKey() {
		String formKey = BpmPlatform.getDefaultProcessEngine().getFormService().getStartFormKey((String)getModelObject().field("id"));
		return FormKey.parse(formKey);
	}

	@Override
	protected ODocument resolveODocument(FormKey formKey) {
		return new ODocument(formKey.getSchemClassName());
	}

}
