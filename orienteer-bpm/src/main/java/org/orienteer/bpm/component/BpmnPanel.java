package org.orienteer.bpm.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.bpm.camunda.handler.ResourceEntityHandler;
import org.orienteer.core.component.AbstractCommandsEnabledPanel;
import org.orienteer.core.component.command.EditCommand;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.wicketbpmnio.component.BpmnModeler;
import org.orienteer.wicketbpmnio.component.BpmnViewer;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Panel to view/edit BPMN 
 */
public class BpmnPanel extends AbstractCommandsEnabledPanel<ODocument> {

	private IModel<ODocument> pdModel;
	private IModel<DisplayMode> modeModel;
	private IModel<String> xmlModel = new LoadableDetachableModel<String>() {

		@Override
		protected String load() {
			ODocument resource = BpmnPanel.this.getModelObject();
			byte[] xmlBytes = resource!=null?(byte[])resource.field("bytes"):null;
			return xmlBytes!=null?new String(xmlBytes):null;
		}
		
		@Override
		public void setObject(String object) {
			super.setObject(object);
			ODocument resource = BpmnPanel.this.getModelObject();
			if(resource!=null) resource.field("bytes", object.getBytes());
		};
	};
	
	private Component panel;
	
	public BpmnPanel(String id, IModel<ODocument> resourceModel, IModel<ODocument> pdModel, IModel<DisplayMode> modeModel) {
		super(id, resourceModel);
		setOutputMarkupId(true);
		this.pdModel = pdModel;
		this.modeModel = modeModel;
		addCommand(new EditODocumentCommand(newCommandId(), resourceModel, modeModel));
		addCommand(new SaveODocumentCommand(this, modeModel, resourceModel) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				ODocument resource = getModelObject();
				if(resource.getIdentity().isNew()) {
					ODocument pd = BpmnPanel.this.pdModel.getObject();
					String resourceName = pd.field("resourceName");
					if(Strings.isEmpty(resourceName)) {
						resourceName = pd.field("name")+".bpmn";
						pd.field("resourceName", resourceName);
						pd.save();
					}
					resource.field("name", resourceName);
					resource.field("deployment", pd.field("deployment"));
				}
				super.onClick(target);
			}
		});
	}
	
	@Override
	protected void onConfigure() {
		DisplayMode mode = modeModel.getObject();
		Component panel = get("panel");
		if(panel==null 
		        || ((panel instanceof BpmnViewer) && !DisplayMode.VIEW.equals(mode))
			    || ((panel instanceof BpmnModeler) && !DisplayMode.EDIT.equals(mode))) {
			panel = DisplayMode.EDIT.equals(mode)
							? new BpmnModeler("panel", xmlModel)
							: new BpmnViewer("panel", xmlModel);
			form.addOrReplace(panel);
		}
		
		super.onConfigure();
	}
	
	@Override
	public void onEvent(IEvent<?> event) {
		if(event.getPayload() instanceof ActionPerformedEvent && event.getType().equals(Broadcast.BUBBLE)) {
			ActionPerformedEvent<?> apEvent = (ActionPerformedEvent<?>)event.getPayload();
			if(apEvent.getCommand()!=null && apEvent.getCommand().isChangingDisplayMode() && apEvent.isAjax()) {
				apEvent.getTarget().add(this);
			}
		}
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		modeModel.detach();
	}

}
