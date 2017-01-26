package org.orienteer.loader.component.widget;

import org.apache.wicket.markup.html.basic.Label;
import org.orienteer.core.web.BasePage;

/**
 * @author Vitaliy Gonchar
 */
public class ReloadPage extends BasePage<Void> {

    public ReloadPage() {
        super();
    }

    @Override
    protected void onInitialize() {
        if (get("title") == null) add(new Label("title", "Reload"));
        add(new Label("info", "Start reload. Please wait..."));
        super.onInitialize();
    }
}
