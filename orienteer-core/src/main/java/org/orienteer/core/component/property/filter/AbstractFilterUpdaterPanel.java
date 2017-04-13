package org.orienteer.core.component.property.filter;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * @author Vitaliy Gonchar
 * @param <T> type of value
 */
public abstract class AbstractFilterUpdaterPanel<T> extends GenericPanel<T> {

    public AbstractFilterUpdaterPanel(String id, IModel<T> valueModel) {
        super(id, valueModel);
        Component component = getFilterComponent(valueModel);
        if (component != null) {
            component.add(AttributeModifier.append("style", "display : table-cell;"));
        }
    }

    protected abstract Component getFilterComponent(IModel<T> valueModel);
}
