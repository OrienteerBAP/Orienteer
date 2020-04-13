package org.orienteer.core.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * Label which break text to new line if text is longer for given width
 */
public class TextBreakPanel extends GenericPanel<String> {

    private int maxWidth = 400;

    public TextBreakPanel(String id) {
        super(id);
    }

    public TextBreakPanel(String id, IModel<String> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label label = new Label("label", getModel());
        label.add(AttributeModifier.append("style", String.format("max-width: %spx;", Integer.toString(maxWidth))));
        add(label);
    }

    public TextBreakPanel setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }
}
