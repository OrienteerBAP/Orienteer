package org.orienteer.core.component.table;

import com.google.common.collect.Lists;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.component.table.filter.IDataFilter;
import org.orienteer.core.component.table.filter.component.OPropertyFilterPanel;

import java.util.Iterator;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 * @param <T> type of values in table
 * @param <S> type of sort parameter
 */
public class DataTableFilterToolbar<T, S> extends AbstractToolbar {

    /**
     * Constructor
     * @param table - data table this toolbar will be attached to
     * @param dataFilter - data filter for table
     */
    public DataTableFilterToolbar(final DataTable<T, S> table, final IDataFilter dataFilter) {
        super(table);
        WebMarkupContainer container = new WebMarkupContainer("filterContainer");
        RefreshingView<IColumn<T, S>> refreshingView = new RefreshingView<IColumn<T, S>>("filters") {
            @Override
            protected Iterator<IModel<IColumn<T, S>>> getItemModels() {
                List<IModel<IColumn<T, S>>> columns = Lists.newArrayList();
                for (IColumn<T, S> column : table.getColumns()) {

                    S sortProperty = column.getSortProperty();
                    if (sortProperty != null) {
                        columns.add(Model.of(column));
                    }
                }
                return columns.iterator();
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void populateItem(Item<IColumn<T, S>> item) {
                IModel<IColumn<T, S>> model = item.getModel();
                IColumn<T, S> column = model.getObject();
                String critery = getCritery(column.getSortProperty());
                WebMarkupContainer filter = new OPropertyFilterPanel("filter", table, dataFilter, critery);

                if (column instanceof AbstractColumn) {
                    AbstractColumn<?, ?> abstractColumn = (AbstractColumn<?, ?>) column;

                    if (abstractColumn.getHeaderColspan() > 1) {
                        filter.add(AttributeModifier.replace("colspan", abstractColumn.getHeaderColspan()));
                    }

                    if (abstractColumn.getHeaderRowspan() > 1) {
                        filter.add(AttributeModifier.replace("rowspan", abstractColumn.getHeaderRowspan()));
                    }
                }

                item.add(filter);
                item.setRenderBodyOnly(true);
            }
        };
        container.add(refreshingView);
        add(container);
    }

    private String getCritery(S sortProperty) {
        Args.notNull(sortProperty, "sortProperty");
        return sortProperty instanceof String ? (String) sortProperty : sortProperty.toString();
    }
}
