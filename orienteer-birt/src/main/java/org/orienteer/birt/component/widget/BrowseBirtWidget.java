package org.orienteer.birt.component.widget;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;


@Widget(id="birt-report", domain="browse", oClass="BrowseBirtWidget", order=10, autoEnable=false)

public class BrowseBirtWidget extends AbstractBirtWidget<OClass>{


	public BrowseBirtWidget(String id, IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		// TODO Auto-generated constructor stub
	}

}
