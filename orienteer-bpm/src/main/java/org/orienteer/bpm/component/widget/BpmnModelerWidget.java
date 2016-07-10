package org.orienteer.bpm.component.widget;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.bpm.camunda.handler.ProcessDefinitionEntityHandler;
import org.orienteer.bpm.camunda.handler.ResourceEntityHandler;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import ru.ydn.wicket.wicketorientdb.model.AbstractConverterModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

/**
 * Widget for BPMN modeling
 */
@Widget(id="bpmn-modeler", domain="document", selector=ProcessDefinitionEntityHandler.OCLASS_NAME, autoEnable=true)
public class BpmnModelerWidget extends AbstractModeAwareWidget<ODocument> {
	
	private OQueryModel<ODocument> resourcesModel; 
	
	public BpmnModelerWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		add(new BpmnModelerPanel("modeler", new PropertyModel<String>(this, "bpmn"), getModeModel()));
		resourcesModel = new OQueryModel<>("select from "+ResourceEntityHandler.OCLASS_NAME+" where name = :resourceName");
		resourcesModel.setParameter("resourceName", new ODocumentPropertyModel<String>(model, "resourceName"));
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.tasks);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.bpmnmodeler");
	}
	
	public String getBpmn() {
		List<ODocument> resources = resourcesModel.getObject();
		if(resources==null || resources.isEmpty()) return null;
		else {
			ODocument resource = resources.get(0);
			byte[] bytes = resource.field("bytes", String.class);
			return new String(bytes);
		}
	}
	
	public void setBpmn(String bpmn) {
		System.out.println("BPMN: "+bpmn);
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		resourcesModel.detach();
	}

}
