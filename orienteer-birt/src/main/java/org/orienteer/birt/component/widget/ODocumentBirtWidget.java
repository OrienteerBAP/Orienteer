package org.orienteer.birt.component.widget;

import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;

@Widget(id="document-birt-report", domain="document", oClass="ODocumentBirtWidget", order=10, autoEnable=false)

public class ODocumentBirtWidget extends AbstractBirtWidget<ODocument>{

	public ODocumentBirtWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
}
