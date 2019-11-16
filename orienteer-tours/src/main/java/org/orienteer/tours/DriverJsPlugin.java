package org.orienteer.tours;

import org.apache.wicket.Page;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

/**
 * Tours JS plugin for Driver JS Lib 
 */
public class DriverJsPlugin implements ITourPlugin {

	private static final WebjarsCssResourceReference DRIVER_JS_CSS = new WebjarsCssResourceReference("driver.js/current/dist/driver.min.css");
	private static final WebjarsJavaScriptResourceReference DRIVER_JS_JS = new WebjarsJavaScriptResourceReference("driver.js/current/dist/driver.min.js");
	
	private static final JavaScriptResourceReference PLUGIN_JS = new JavaScriptResourceReference(DriverJsPlugin.class, "driverjs/driverjs-plugin.js");

	
	@Override
	public void renderHeader(Page page, IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(DRIVER_JS_CSS));
		response.render(JavaScriptHeaderItem.forReference(DRIVER_JS_JS));
		
		response.render(JavaScriptHeaderItem.forReference(PLUGIN_JS));
		
		response.render(CssHeaderItem.forCSS("div#driver-highlighted-element-stage, div#driver-page-overlay {\r\n" + 
				"  background: transparent !important;\r\n" + 
				"  outline: 5000px solid rgba(0, 0, 0, .75)\r\n" + 
				"}", "FixAnimatedDriverJS"));
	}

}
