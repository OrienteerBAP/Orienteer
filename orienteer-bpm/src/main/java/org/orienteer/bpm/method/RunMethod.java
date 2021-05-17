package org.orienteer.bpm.method;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.IModel;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.orienteer.bpm.camunda.handler.ProcessDefinitionEntityHandler;
import org.orienteer.bpm.component.widget.FormKey;
import org.orienteer.bpm.component.widget.ProcessDefinitionFormWidget;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.ODocumentFilter;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.methods.AbstractOMethod;
import org.orienteer.core.web.ODocumentPage;

import com.orientechnologies.orient.core.record.impl.ODocument;

@OMethod(titleKey = "command.run", 
		 icon = FAIconType.play,
		 bootstrap = BootstrapType.SUCCESS,
		 filters = {
	                @OFilter(fClass = ODocumentFilter.class, fData = ProcessDefinitionEntityHandler.OCLASS_NAME),
	                @OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE")
	        },
		 order = 0)
public class RunMethod extends AbstractOMethod {

	@Override
	public Command<?> createCommand(String id) {
		return new AjaxCommand<Object>(id, getTitleModel()) {
			@Override
			protected void onInitialize() {
				super.onInitialize();
				applySettings(this);
			}
			
			@Override
			public void onClick(Optional<AjaxRequestTarget> targetOptional) {
				IMethodContext ctx = getContext();
				ODocument processDefinition = (ODocument)ctx.getCurrentWidget().getModelObject();
				FormKey formKey = FormKey.parse(BpmPlatform.getDefaultProcessEngine().getFormService().getStartFormKey((String)processDefinition.field("id")));
				if(formKey.isValid()) {
					ctx.showFeedback(FeedbackMessage.WARNING, "command.run.formrequired", null);
					setResponsePage(ODocumentPage.class, 
							ODocumentPage.getPageParameters(processDefinition, DisplayMode.EDIT)
									.add("tab", "form"));
				} else {
					ProcessInstance instance = BpmPlatform.getDefaultProcessEngine().getRuntimeService()
												.startProcessInstanceById((String)processDefinition.field("id"));
					
					ctx.showFeedback(FeedbackMessage.INFO, "command.run.started", null);
				}
			}
		};
	}

}
