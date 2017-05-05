package org.orienteer.core.web;

import org.apache.wicket.markup.html.WebPage;

/**
 * Page which represents Orienteer reloading.
 * Javascript create requests to server and when server is available (GET request receives a response HTTP code 200)
 * JS forwards user to main Orienteer page ('/' Login page or Schema page)
 */
public class OrienteerReloadPage extends WebPage {

}
