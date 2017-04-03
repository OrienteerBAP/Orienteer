package org.orienteer.core.component.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.filter.IDataFilter;
import org.orienteer.core.component.visualizer.IVisualizer;

/**
 * @author Vitaliy Gonchar
 * @param <T> - type of values in table
 * @param <S> - type of sort parameters
 */
class DataTableFilterToolbar<T, S> extends AbstractToolbar {

    private DataTable<T, ?> table;

    private IDataFilter<?> dataFilter;
    private IModel<DisplayMode> modeModel;

    private static final String ID = "filter";

    DataTableFilterToolbar(DataTable<T, S> table) {
        super(table);
        this.table = table;
        setOutputMarkupPlaceholderTag(true);
    }


    void setDataFilter(IDataFilter<?> dataFilter, IModel<DisplayMode> modeModel) {
        this.dataFilter = Args.notNull(dataFilter, "dataFilter");
        this.modeModel = Args.notNull(modeModel, "modeModel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataTable<T, ?> getTable() {
        return (DataTable<T, ?>)super.getTable();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        WebMarkupContainer container = new WebMarkupContainer("td");
        container.add(AttributeModifier.replace("colspan", Model.of(String.valueOf(table.getColumns().size()))));
        IVisualizer visualizer = dataFilter != null ? dataFilter.getVisualizer() : null;
        if (visualizer != null) {
            Component component = visualizer.createComponent(ID, modeModel.getObject(),
                    null, null, dataFilter.getFilterParam());
            container.add(component);
        } else {
            container.add(new Label(ID, Model.of("")).setVisible(false).setOutputMarkupPlaceholderTag(true));
        }
        addOrReplace(container);
    }
}
