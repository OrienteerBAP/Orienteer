package org.orienteer.birt.component.widget;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * BIRT report for list of objects
 */

@Widget(id="birt-report", domain="browse", oClass=BrowseBirtWidget.OCLASS_NAME, order=10, autoEnable=false)
public class BrowseBirtWidget extends AbstractBirtWidget<OClass>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String OCLASS_NAME = "BrowseBirtWidget";

	public BrowseBirtWidget(String id, IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel,null);
	}

}
