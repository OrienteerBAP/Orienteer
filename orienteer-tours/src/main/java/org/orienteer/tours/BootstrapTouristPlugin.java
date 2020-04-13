package org.orienteer.tours;

import org.apache.wicket.Page;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.orienteer.core.web.BasePage;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

/**
 * Tours JS plugin for BootstrapTourist JS Lib 
 */
public class BootstrapTouristPlugin implements ITourPlugin {
	
	private static final CssResourceReference TOURIST_CSS = new CssResourceReference(BootstrapTouristPlugin.class, "bootstrap-tourist/bootstrap-tourist.css");
	
	private static final JavaScriptResourceReference TOURIST_JS = new JavaScriptResourceReference(BootstrapTouristPlugin.class, "bootstrap-tourist/bootstrap-tourist.js");

	private static final JavaScriptResourceReference PLUGIN_JS = new JavaScriptResourceReference(BootstrapTouristPlugin.class, "bootstrap-tourist/bootstrap-tourist-plugin.js");

	@Override
	public void renderHeader(Page page, IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(TOURIST_CSS));
		response.render(JavaScriptHeaderItem.forReference(TOURIST_JS));
		response.render(JavaScriptHeaderItem.forReference(PLUGIN_JS));
	}

}
