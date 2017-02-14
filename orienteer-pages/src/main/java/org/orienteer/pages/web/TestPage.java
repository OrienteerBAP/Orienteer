package org.orienteer.pages.web;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.orienteer.core.web.OrienteerBasePage;

/**
 * @author Vitaliy Gonchar
 */
public class TestPage extends OrienteerBasePage<Void> {

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("label", "Hello world!"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(
                new WebjarsCssResourceReference("weather-icons/1.3.2/css/weather-icons.css")));
    }
}
