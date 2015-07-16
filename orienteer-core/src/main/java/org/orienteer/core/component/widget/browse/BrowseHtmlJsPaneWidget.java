package org.orienteer.core.component.widget.browse;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;
import org.orienteer.core.component.widget.AbstractHtmlJsPaneWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget for free HTML/JS widget for browse page
 */
@Widget(id="browse-html-js-pane", domain="browse", oClass=AbstractHtmlJsPaneWidget.WIDGET_OCLASS_NAME, order=10, autoEnable=false)
public class BrowseHtmlJsPaneWidget extends AbstractHtmlJsPaneWidget<OClass> {

	public BrowseHtmlJsPaneWidget(String id, IModel<OClass> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
	protected String interpolate(String content) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("className", getModelObject().getName());
		return MapVariableInterpolator.interpolate(super.interpolate(content), params);
	}
	
}
