package org.orienteer.core.component;

import java.util.Map;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.orienteer.core.util.CommonUtils;

/**
 * Ajax Indicator for a whole page
 */
public class AjaxIndicator extends Panel {

	public AjaxIndicator(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		checkComponentTag(tag, "div");
		tag.append("style", "display: none", "; ");
		tag.append("class", "ajax-indicator", " ");
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		TextTemplate template = new PackageTextTemplate(AjaxIndicator.class, "ajax-indicator.js");
		template.interpolate(CommonUtils.<String, Object>toMap("componentId", getMarkupId()));
		response.render(OnDomReadyHeaderItem.forScript(template.getString()));
	}
	
}
