package org.orienteer.core.component.table.filter.component;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.filter.IDataFilter;
import org.orienteer.core.component.table.filter.updater.IFilterUpdater;
import org.orienteer.core.component.visualizer.IVisualizer;

/**
 * @author Vitaliy Gonchar
 * @param <T> type of values in table
 * @param <S> type of sort parameter
 */
public class OPropertyFilterPanel<T, S> extends Panel {

    @SuppressWarnings("unchecked")
    public OPropertyFilterPanel(String id, DataTable<T, S> table, IDataFilter dataFilter,  String critery) {
        super(id);
        IVisualizer visualizer = dataFilter.getVisualizer();
        IModel<OProperty> property = dataFilter.getOPropertyModelByName(critery);
        if (property != null) {
            Component component = visualizer.createComponent("filter", DisplayMode.EDIT,
                    null, property, dataFilter.getDataFilters().get(property));
            if (component instanceof IFilterUpdater) {
                IFilterUpdater updater = (IFilterUpdater) component;
                updater.configure(table, dataFilter);
            }
            add(component);
        } else {
            String msg = "Filters not supported for this critery: %s!";
            add(new Label("filter", Model.of(String.format(msg, critery))));
        }
    }
}
