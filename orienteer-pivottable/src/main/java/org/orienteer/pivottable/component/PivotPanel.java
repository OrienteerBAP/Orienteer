package org.orienteer.pivottable.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.support.jquery.JQueryDashboardSupport;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

/**
 * Panel to show pivot
 */
public class PivotPanel extends GenericPanel<String> {
	
	private static final WebjarsCssResourceReference PIVOT_CSS = new WebjarsCssResourceReference("/webjars/pivottable/current/dist/pivot.min.css");
	private static final WebjarsJavaScriptResourceReference PIVOT_JS = new WebjarsJavaScriptResourceReference("/webjars/pivottable/current/dist/pivot.min.js");
	private static final CssResourceReference PIVOT_CSS_FIX = new CssResourceReference(PivotPanel.class, "pivottable.css");

	private final IModel<String> configModel;
	private final IModel<DisplayMode> modeModel;
	
	public PivotPanel(String id, IModel<String> urlModel, IModel<DisplayMode> modeModel, IModel<String> configModel) {
		super(id, urlModel);
		setOutputMarkupId(true);
		this.modeModel = modeModel;
		this.configModel = configModel;
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		modeModel.detach();
		configModel.detach();
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(PIVOT_CSS));
		response.render(CssHeaderItem.forReference(PIVOT_CSS_FIX));
		response.render(JavaScriptHeaderItem.forReference(JQueryDashboardSupport.JQUERY_UI_JS));
		response.render(JavaScriptHeaderItem.forReference(PIVOT_JS));
		TextTemplate template = new PackageTextTemplate(PivotPanel.class, "pivottable.tmpl.js");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("componentId", getMarkupId());
		params.put("dataUrl", getModelObject().replace("'", "\\'"));
		params.put("config", Strings.defaultIfEmpty(configModel.getObject(), "{}"));
		params.put("editMode", DisplayMode.EDIT.equals(modeModel.getObject()));
		template.interpolate(params);
		response.render(OnDomReadyHeaderItem.forScript(template.asString()));
	}
	
}
