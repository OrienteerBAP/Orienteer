package org.orienteer.bpm.component.widget;

import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.bpm.camunda.handler.ProcessDefinitionEntityHandler;
import org.orienteer.bpm.camunda.handler.ResourceEntityHandler;
import org.orienteer.bpm.component.BpmnPanel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.AbstractConverterModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

/**
 * Widget for BPMN modeling
 */
@Widget(id="bpmn-modeler", domain="document", selector=ProcessDefinitionEntityHandler.OCLASS_NAME, autoEnable=true)
public class BpmnModelerWidget extends AbstractModeAwareWidget<ODocument> {
	
	public BpmnModelerWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		OQueryModel<ODocument> resourcesModel = new OQueryModel<>("select from "+ResourceEntityHandler.OCLASS_NAME+" where name = :resourceName");
		resourcesModel.setParameter("resourceName", new ODocumentPropertyModel<String>(model, "resourceName"));
		IModel<ODocument> resourceModel = new AbstractConverterModel<List<ODocument>, ODocument>(resourcesModel) {

			@Override
			protected ODocument doForward(List<ODocument> a) {
				if(a==null || a.isEmpty()){
					ODocument pd = getModelObject();
					String resourceName = pd.field("resourceName");
					if(Strings.isEmpty(resourceName)) {
						resourceName = pd.field("name")+".bpmn";
						pd.field("resourceName", resourceName);
						pd.save();
					}
					ODocument resource = new ODocument(ResourceEntityHandler.OCLASS_NAME);
					resource.field("name", resourceName);
					resource.field("deployment", pd.field("deployment"));
					return resource;
				} else {
					return a.get(0);
				}
			}
		};
		add(new BpmnPanel("bpmnPanel", resourceModel, getModeModel()));
	}
	
	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.tasks);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.bpmnmodeler");
	}
}
