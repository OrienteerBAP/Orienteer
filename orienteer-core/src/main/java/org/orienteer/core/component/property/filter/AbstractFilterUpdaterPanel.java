package org.orienteer.core.component.property.filter;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.component.table.filter.IDataFilter;
import org.orienteer.core.component.table.filter.updater.IFilterUpdater;

/**
 * @author Vitaliy Gonchar
 * @param <T> type of value
 */
public abstract class AbstractFilterUpdaterPanel<T> extends GenericPanel<T> implements IFilterUpdater {

    private Component componentForUpdate;
    private IDataFilter dataFilter;

    public AbstractFilterUpdaterPanel(String id, IModel<T> valueModel) {
        super(id, valueModel);
        Component component = getFilterComponent(valueModel);
        if (component != null) {
            addFormSubmitBehavior(component);
            component.add(AttributeModifier.append("style", "display : table-cell;"));
        }
    }

    protected abstract Component getFilterComponent(IModel<T> valueModel);

    protected final void addFormSubmitBehavior(Component component) {
        if (component != null) {
            component.add(new AjaxFormSubmitBehavior("change") {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    update(target);
                }
            });
        }
    }

    @Override
    public final void configure(Component componentForUpdate, IDataFilter dataFilter) {
        Args.notNull(componentForUpdate, "componentForUpdate");
        Args.notNull(dataFilter, "dataFilter");
        this.componentForUpdate = componentForUpdate;
        this.dataFilter = dataFilter;
    }

    @Override
    public final void update(AjaxRequestTarget target) {
        Args.notNull(componentForUpdate, "componentForUpdate");
        Args.notNull(dataFilter, "dataFilter");
        dataFilter.updateDataProvider();
        target.add(componentForUpdate);
    }
}
