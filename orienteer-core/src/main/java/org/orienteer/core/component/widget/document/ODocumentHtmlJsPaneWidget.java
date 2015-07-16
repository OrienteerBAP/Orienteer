package org.orienteer.core.component.widget.document;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;
import org.orienteer.core.component.widget.AbstractHtmlJsPaneWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget for free HTML/JS widget for documents page
 */
@Widget(id="document-html-js-pane", domain="document", oClass=AbstractHtmlJsPaneWidget.WIDGET_OCLASS_NAME, autoEnable=false)
public class ODocumentHtmlJsPaneWidget extends
		AbstractHtmlJsPaneWidget<ODocument> {

	public ODocumentHtmlJsPaneWidget(String id, IModel<ODocument> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}

	protected String interpolate(String content) {
		return MapVariableInterpolator.interpolate(super.interpolate(content), new ODocumentMapWrapper(getModelObject()));
	}
	
}
