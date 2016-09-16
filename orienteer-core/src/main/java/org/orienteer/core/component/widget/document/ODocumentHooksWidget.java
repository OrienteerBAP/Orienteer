/**
 * 
 */
package org.orienteer.core.component.widget.document;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author Asm
 *
 */

@Widget(domain="document", id = ODocumentHooksWidget.WIDGET_TYPE_ID, order=30,autoEnable=true)
public class ODocumentHooksWidget extends AbstractModeAwareWidget<ODocument> {
	public static final String WIDGET_TYPE_ID = "documentHooks";

	public ODocumentHooksWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected FAIcon newIcon(String id) {
		// TODO Auto-generated method stub
        return new FAIcon(id, FAIconType.list);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("widget.document.hooks");
	}
}
