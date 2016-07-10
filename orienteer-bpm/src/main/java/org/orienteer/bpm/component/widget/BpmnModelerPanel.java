package org.orienteer.bpm.component.widget;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.orienteer.core.component.AbstractCommandsEnabledPanel;
import org.orienteer.core.component.command.EditCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.util.CommonUtils;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;


/**
 * Panel to allow view/edit BPMN 
 */
public class BpmnModelerPanel extends AbstractCommandsEnabledPanel<String> {
	
	private static final WebjarsCssResourceReference BPMN_DIAGRAM_CSS = new WebjarsCssResourceReference("bpmn-js/current/dist/assets/diagram-js.css");
	private static final WebjarsCssResourceReference BPMN_EMBEDDED_CSS = new WebjarsCssResourceReference("bpmn-js/current/dist/assets/bpmn-font/css/bpmn-embedded.css");
	private static final WebjarsCssResourceReference BPMN_CSS = new WebjarsCssResourceReference("bpmn-js/current/dist/assets/bpmn-font/css/bpmn.css");
	private static final WebjarsJavaScriptResourceReference BPMN_VIEWER_JS = new WebjarsJavaScriptResourceReference("bpmn-js/current/dist/bpmn-viewer.js");
	private static final WebjarsJavaScriptResourceReference BPMN_MODELER_JS = new WebjarsJavaScriptResourceReference("bpmn-js/current/dist/bpmn-modeler.js");
	
	private IModel<DisplayMode> modeModel;

	public BpmnModelerPanel(String id, IModel<String> model, IModel<DisplayMode> modeModel) {
		super(id, model);
		this.modeModel = modeModel!=null?modeModel:DisplayMode.VIEW.asModel();
		setOutputMarkupId(true);
//		addCommand(new EditCommand<String>(newCommandId(), this.modeModel));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(BPMN_DIAGRAM_CSS));
		response.render(CssHeaderItem.forReference(BPMN_EMBEDDED_CSS));
//		response.render(CssHeaderItem.forReference(BPMN_CSS));
//		if(DisplayMode.EDIT.equals(modeModel.getObject())) {
			response.render(JavaScriptHeaderItem.forReference(BPMN_VIEWER_JS));
			response.render(JavaScriptHeaderItem.forReference(BPMN_MODELER_JS));
//		} else {
//		}
		TextTemplate template = new PackageTextTemplate(BpmnModelerPanel.class, "modeler.tmpl.js");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("componentId", getMarkupId());
		params.put("editMode", DisplayMode.EDIT.equals(modeModel.getObject()));
		String xml = getModelObject();
		params.put("xml", CommonUtils.escapeAndWrapAsJavaScriptString(xml));
		response.render(OnDomReadyHeaderItem.forScript(template.asString(params)));
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.append("class", "modeler", " ");
	}
	
}
