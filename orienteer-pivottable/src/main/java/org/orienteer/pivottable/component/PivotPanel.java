package org.orienteer.pivottable.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.Method;
import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.support.jquery.JQueryDashboardSupport;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.MapMaker;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

/**
 * Panel to show pivot
 */
public class PivotPanel extends GenericPanel<String> {
	
	private static final WebjarsCssResourceReference PIVOT_CSS = new WebjarsCssResourceReference("/webjars/pivottable/current/dist/pivot.min.css");
	private static final WebjarsJavaScriptResourceReference PIVOT_JS = new WebjarsJavaScriptResourceReference("/webjars/pivottable/current/dist/pivot.min.js");
	private static final CssResourceReference PIVOT_CSS_FIX = new CssResourceReference(PivotPanel.class, "pivottable.css");

	
	private static final WebjarsJavaScriptResourceReference D3_JS = new WebjarsJavaScriptResourceReference("/webjars/d3/current/d3.min.js");
	private static final WebjarsJavaScriptResourceReference D3_RENDERERS_JS = new WebjarsJavaScriptResourceReference("/webjars/pivottable/current/dist/d3_renderers.min.js");
	
	private static final WebjarsCssResourceReference C3_CSS = new WebjarsCssResourceReference("/webjars/c3/current/c3.min.css");
	private static final WebjarsJavaScriptResourceReference C3_JS = new WebjarsJavaScriptResourceReference("/webjars/c3/current/c3.min.js");
	private static final WebjarsJavaScriptResourceReference C3_RENDERERS_JS = new WebjarsJavaScriptResourceReference("/webjars/pivottable/current/dist/c3_renderers.min.js");
	
	private static final List<String> SUPPORTED_LANGS = Arrays.asList("en", "es", "fr", "nl", "pt", "ru", "tr", "zh");
	private static final Map<String, WebjarsJavaScriptResourceReference> LANGUAGES_MAP = new HashMap<>();
	
	private final IModel<String> configModel;
	private final IModel<DisplayMode> modeModel;
	private final UpdatePivotTableBehavior updatePivotTableBehavior;
	
	private class UpdatePivotTableBehavior extends AbstractDefaultAjaxBehavior {

		@Override
		protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
			super.updateAjaxAttributes(attributes);
			attributes.getDynamicExtraParameters().add("return {config: JSON.stringify(cfg)};");
			attributes.setMethod(Method.POST);
		}
		
		@Override
		protected void respond(AjaxRequestTarget target) {
			if(configModel!=null) 
				configModel.setObject(RequestCycle.get().getRequest().getRequestParameters()
					 .getParameterValue("config").toString());
		}
		
		@Override
		public boolean isEnabled(Component component) {
			DisplayMode mode = modeModel!=null?modeModel.getObject():DisplayMode.VIEW;
			return DisplayMode.EDIT.equals(mode);
		}
		
	}
	
	public PivotPanel(String id, IModel<String> urlModel, IModel<DisplayMode> modeModel, IModel<String> configModel) {
		super(id, urlModel);
		setOutputMarkupId(true);
		this.modeModel = modeModel;
		this.configModel = configModel;
		add(updatePivotTableBehavior = new UpdatePivotTableBehavior());
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		modeModel.detach();
		configModel.detach();
	}
	
	protected JavaScriptResourceReference getLocalizationJSResource(String lang) {
		if(Strings.isEmpty(lang) || "en".equals(lang)) return null;
		WebjarsJavaScriptResourceReference ret = LANGUAGES_MAP.get(lang);
		if(ret==null) {
			ret = new WebjarsJavaScriptResourceReference("/webjars/pivottable/current/dist/pivot."+lang+".min.js");
			LANGUAGES_MAP.put(lang, ret);
		}
		return ret;
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(PIVOT_CSS));
		response.render(CssHeaderItem.forReference(PIVOT_CSS_FIX));
		response.render(CssHeaderItem.forReference(C3_CSS));
		response.render(JavaScriptHeaderItem.forReference(JQueryDashboardSupport.JQUERY_UI_JS));
		response.render(JavaScriptHeaderItem.forReference(PIVOT_JS));
		response.render(JavaScriptHeaderItem.forReference(D3_JS));
		response.render(JavaScriptHeaderItem.forReference(C3_JS));
		response.render(JavaScriptHeaderItem.forReference(D3_RENDERERS_JS));
		response.render(JavaScriptHeaderItem.forReference(C3_RENDERERS_JS));
		String lang = getLocale().getLanguage();
		if(SUPPORTED_LANGS.indexOf(lang)<0) lang = "en";
		JavaScriptResourceReference langRes = getLocalizationJSResource(lang);
		if(langRes!=null) response.render(JavaScriptHeaderItem.forReference(langRes));
		TextTemplate template = new PackageTextTemplate(PivotPanel.class, "pivottable.tmpl.js");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("componentId", getMarkupId());
		params.put("dataUrl", getModelObject().replace("'", "\\'"));
		params.put("config", Strings.defaultIfEmpty(configModel.getObject(), "{}"));
		params.put("editMode", DisplayMode.EDIT.equals(modeModel.getObject()));
		params.put("callBackScript", updatePivotTableBehavior.getCallbackScript());
		params.put("language", lang);
		template.interpolate(params);
		response.render(OnDomReadyHeaderItem.forScript(template.asString()));
	}
	
}
