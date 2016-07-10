package org.orienteer.bpm.component.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.orienteer.bpm.camunda.handler.ProcessDefinitionEntityHandler;
import org.orienteer.bpm.camunda.handler.ResourceEntityHandler;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import ru.ydn.wicket.wicketorientdb.model.AbstractConverterModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

/**
 * Widget for BPMN modeling
 */
@Widget(id="bpmn-modeler", domain="document", selector=ProcessDefinitionEntityHandler.OCLASS_NAME, autoEnable=true)
public class BpmnModelerWidget extends AbstractWidget<ODocument> {
	
	private static final WebjarsCssResourceReference BPMN_DIAGRAM_CSS = new WebjarsCssResourceReference("bpmn-js/current/dist/assets/diagram-js.css");
	private static final WebjarsCssResourceReference BPMN_EMBEDDED_CSS = new WebjarsCssResourceReference("bpmn-js/current/dist/assets/bpmn-font/css/bpmn-embedded.css");
	private static final WebjarsCssResourceReference BPMN_CSS = new WebjarsCssResourceReference("bpmn-js/current/dist/assets/bpmn-font/css/bpmn.css");
	private static final WebjarsJavaScriptResourceReference BPMN_VIEWER_JS = new WebjarsJavaScriptResourceReference("bpmn-js/current/dist/bpmn-viewer.js");
	private static final WebjarsJavaScriptResourceReference BPMN_MODELER_JS = new WebjarsJavaScriptResourceReference("bpmn-js/current/dist/bpmn-modeler.js");
	
	private IModel<ODocument> resourceModel;
	
	private HiddenField<String> xmlField;
	
	public BpmnModelerWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		OQueryModel<ODocument> resourcesModel = new OQueryModel<>("select from "+ResourceEntityHandler.OCLASS_NAME+" where name = :resourceName");
		resourcesModel.setParameter("resourceName", new ODocumentPropertyModel<String>(model, "resourceName"));
		resourceModel = new AbstractConverterModel<List<ODocument>, ODocument>(resourcesModel) {

			@Override
			protected ODocument doForward(List<ODocument> a) {
				return a==null || a.isEmpty() ? null: a.get(0);
			}
		};
		Form<?> form = new Form<>("form");
		form.add(xmlField = new HiddenField<>("xml", Model.of((String)null)));
		xmlField.setOutputMarkupId(true);
		xmlField.setModelObject(readBpmn());
		form.add(new AjaxSubmitLink("save") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				saveBpmn(xmlField.getModelObject());
			}
		});
		add(form);
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.tasks);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.bpmnmodeler");
	}
	
	public String readBpmn() {
		ODocument resource = resourceModel.getObject();
		byte[] xmlBytes = resource!=null?(byte[])resource.field("bytes"):null;
		return xmlBytes!=null?new String(xmlBytes):null;
	}
	
	public void saveBpmn(String bpmn) {
		ODocument resource = resourceModel.getObject();
		if(resource!=null) {
			resource.field("bytes", bpmn.getBytes());
		} else {
			//TODO: Implement creation of new resource
		}
		resource.save();
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(BPMN_DIAGRAM_CSS));
//		response.render(CssHeaderItem.forReference(BPMN_EMBEDDED_CSS));
		response.render(CssHeaderItem.forReference(BPMN_CSS));
//		if(DisplayMode.EDIT.equals(modeModel.getObject())) {
			response.render(JavaScriptHeaderItem.forReference(BPMN_VIEWER_JS));
			response.render(JavaScriptHeaderItem.forReference(BPMN_MODELER_JS));
//		} else {
//		}
		ODocument resource = resourceModel.getObject();
		TextTemplate template = new PackageTextTemplate(BpmnModelerWidget.class, "modeler.tmpl.js");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("componentId", getMarkupId());
		params.put("xmlFieldComponentId", xmlField.getMarkupId());
		params.put("canEdit", canEdit(getModelObject(), resource));
		response.render(OnDomReadyHeaderItem.forScript(template.asString(params)));
	}
	
	protected boolean canEdit(ODocument processDefinition, ODocument resource) {
		return OSecurityHelper.isAllowed(processDefinition, OrientPermission.UPDATE)
				&& OSecurityHelper.isAllowed(resource, OrientPermission.UPDATE);
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		resourceModel.detach();
	}

}
