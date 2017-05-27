package org.orienteer.core.component.property.filter;

import com.google.inject.Inject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.orienteer.core.service.IMarkupProvider;

/**
 * Filter panel
 */
public class FilterOPropertyPanel extends Panel {

    @Inject
    private IMarkupProvider markupProvider;

    public FilterOPropertyPanel(String id, Component component) {
        super(id);
        add(AttributeModifier.append("style", "display : table-cell;"));
        add(component);
    }

    @Override
    public IMarkupFragment getMarkup(Component child) {
        return child != null ? markupProvider.provideMarkup(child) : super.getMarkup(child);
    }
}
