package org.orienteer.birt.component.widget;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * BIRT report for single ODocument
 *
 */
@Widget(id="document-birt-report", domain="document", oClass=ODocumentBirtWidget.OCLASS_NAME, order=10, autoEnable=false)
public class ODocumentBirtWidget extends AbstractBirtWidget<ODocument>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String OCLASS_NAME = "ODocumentBirtWidget";

	public ODocumentBirtWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel,makeParameters(model));
	}
	
	private static Map<String, Object> makeParameters(IModel<ODocument> model){
		HashMap<String, Object> parameters = new HashMap<String,Object>();
		parameters.put("rid", model.getObject().getIdentity().toString());
		return parameters;
	}
}
