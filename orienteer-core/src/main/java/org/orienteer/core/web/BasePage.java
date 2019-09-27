package org.orienteer.core.web;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxClientInfoBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.settings.JavaScriptLibrarySettings;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.AjaxIndicator;
import org.orienteer.core.component.OModulesLoadFailedPanel;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.widget.support.jquery.JQueryDashboardSupport;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import java.util.List;
import java.util.Locale;

/**
 * Root {@link WebPage} for Orienteer enabled pages.
 * Provide main resources for a header and basic methods to work with perspectives
 *
 * @param <T> type of a main object for this page
 */
public abstract class BasePage<T> extends GenericWebPage<T>
{
	private static final long serialVersionUID = 1L;

	public static final CssResourceReference BOOTSTRAP_CSS = new WebjarsCssResourceReference("bootstrap/current/css/bootstrap.min.css");
	public static final CssResourceReference FONT_AWESOME_CSS = new WebjarsCssResourceReference("font-awesome/current/css/font-awesome.min.css");
	public static final CssResourceReference SIMPLE_LINE_ICONS_CSS = new WebjarsCssResourceReference("simple-line-icons/current/css/simple-line-icons.css");
	public static final CssResourceReference COREUI_CSS = new WebjarsCssResourceReference("coreui__ajax/current/AJAX_Full_Project_GULP/src/css/style.min.css");
	public static final CssResourceReference ORIENTEER_COREUI_CSS = new CssResourceReference(BasePage.class, "orienteer-coreui.css");

	public static final JavaScriptResourceReference BOOTSTRAP_JS = new WebjarsJavaScriptResourceReference("bootstrap/current/js/bootstrap.bundle.min.js");
	public static final JavaScriptResourceReference TETHER_JS = new WebjarsJavaScriptResourceReference("tether/current/js/tether.min.js");
	public static final JavaScriptResourceReference PACE_JS = new WebjarsJavaScriptResourceReference("pace/current/pace.min.js");
	public static final JavaScriptResourceReference COREUI_JS = new WebjarsJavaScriptResourceReference("coreui__ajax/current/Static_Starter_GULP/src/js/app.js");


	protected static final CssResourceReference BOOTSTRAP_DATE_PICKER_CSS       = new WebjarsCssResourceReference("bootstrap-datepicker/current/css/bootstrap-datepicker3.min.css");
	protected static final JavaScriptResourceReference BOOTSTRAP_DATE_PICKER_JS = new WebjarsJavaScriptResourceReference("bootstrap-datepicker/current/js/bootstrap-datepicker.min.js");
	protected static final String BOOTSTRAP_DATEPICKER_LOCALE                   = "bootstrap-datepicker/current/locales/bootstrap-datepicker.%s.min.js";


	@Inject
	private PerspectivesModule perspectivesModule;
	
	private RepeatingView uiPlugins;
	
	public BasePage()
	{
		super();
		initialize();
	}

	public BasePage(IModel<T> model)
	{
		super(model);
		initialize();
	}

	public BasePage(PageParameters parameters)
	{
		super(parameters);
		if(parameters!=null && !parameters.isEmpty())
		{
			IModel<T> model = resolveByPageParameters(parameters);
			if(model!=null) setModel(model);
			String perspective = parameters.get("_perspective").toOptionalString();
			if(!Strings.isEmpty(perspective))
			{
				perspectivesModule.getPerspectiveByAliasAsDocument(getDatabase(), perspective)
						.ifPresent(perspectiveDoc -> OrienteerWebSession.get().setPerspecive(perspectiveDoc));
			}
		}
		initialize();
	}

	protected IModel<T> resolveByPageParameters(PageParameters pageParameters)
	{
		return null;
	}


	public void initialize()
	{
		//TO BO sure that DB was initialized
		getDatabase();
		uiPlugins = new RepeatingView("uiPlugins");
		add(uiPlugins);
		if(isClientInfoRequired() && !OrienteerWebSession.get().isClientInfoAvailable()) add(new AjaxClientInfoBehavior());
	}
	
	protected boolean isClientInfoRequired() {
		return true;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		TransparentWebMarkupContainer body;
		add(body = new TransparentWebMarkupContainer("body"));
		body.add(new AttributeAppender("class", " "+getBodyAppSubClasses()));

		if(get("title")==null) add(new Label("title", getTitleModel()).add(UpdateOnActionPerformedEventBehavior.INSTANCE_ALWAYS_FOR_CHANGING));
		IModel<String> poweredByModel = new StringResourceModel("poweredby").setParameters(
				OrienteerWebApplication.get().getVersion(), OrienteerWebSession.get().isSignedIn() ? OrienteerWebApplication.get().getLoadModeInfo() : "");
		if(get("modulesFailed")==null) add(new OModulesLoadFailedPanel("modulesFailed"));
		if(get("poweredBy")==null) add(new Label("poweredBy", poweredByModel).setEscapeModelStrings(false));
		if(get("footer")==null) add(new Label("footer", new PropertyModel<List<ODocument>>(new PropertyModel<ODocument>(this, "perspective"), "footer"))
									.setEscapeModelStrings(false).setRenderBodyOnly(true));
		if(get("indicator")==null) add(new AjaxIndicator("indicator"));
		//add(new BodyTagAttributeModifier("class", Model.of("sidebar"), this));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(BOOTSTRAP_CSS));
		response.render(CssHeaderItem.forReference(FONT_AWESOME_CSS));
		response.render(CssHeaderItem.forReference(SIMPLE_LINE_ICONS_CSS));
		response.render(CssHeaderItem.forReference(COREUI_CSS));
		response.render(CssHeaderItem.forReference(ORIENTEER_COREUI_CSS));
		super.renderHead(response);
		addBootstrapDatepicker(response);
		JavaScriptLibrarySettings javaScriptSettings =
				getApplication().getJavaScriptLibrarySettings();
		response.render(JavaScriptHeaderItem.forReference(javaScriptSettings.getJQueryReference()));
		response.render(JavaScriptHeaderItem.forReference(JQueryDashboardSupport.JQUERY_UI_JS));
		response.render(JavaScriptHeaderItem.forReference(BOOTSTRAP_JS));
		response.render(JavaScriptHeaderItem.forReference(TETHER_JS));
		response.render(JavaScriptHeaderItem.forReference(PACE_JS));
		response.render(JavaScriptHeaderItem.forReference(COREUI_JS).setDefer(true));
		response.render(OnDomReadyHeaderItem.forScript("$(function () {$('[data-toggle=\"tooltip\"]').tooltip()})"));
	}

	protected String getBodyAppSubClasses(){
		return "header-fixed sidebar-fixed aside-menu-fixed aside-menu-hidden";
	}
	
	private void addBootstrapDatepicker(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(BOOTSTRAP_DATE_PICKER_CSS));
		response.render(JavaScriptHeaderItem.forReference(BOOTSTRAP_DATE_PICKER_JS));
		String language = getLocale().getLanguage();
		if (!language.equals(Locale.ENGLISH.getLanguage())) {
			response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference(
					String.format(BOOTSTRAP_DATEPICKER_LOCALE, language))));
		}
	}

	public ODatabaseDocument getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}
	
	public OSchema getSchema()
	{
		return OrientDbWebSession.get().getSchema();
	}
	
	public IModel<String> getTitleModel()
	{
		return new ResourceModel("default.title");
	}
	
	public ODocument getPerspective()
	{
		return OrienteerWebSession.get().getPerspective();
	}
	
	public RepeatingView getUiPluginsComponentsHolder() {
		return uiPlugins;
	}
	
	public String nextUiPluginComponentId() { 
		return uiPlugins.newChildId();
	}
	
	public void addUiPlugin(Component uiPluginPanel) {
		uiPlugins.add(uiPluginPanel);
	}

}
