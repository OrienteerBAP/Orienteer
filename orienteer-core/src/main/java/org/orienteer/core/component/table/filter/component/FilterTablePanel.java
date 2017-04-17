package org.orienteer.core.component.table.filter.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.component.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.filter.AbstractFilteredDataProvider;
import ru.ydn.wicket.wicketorientdb.filter.IODataFilter;

import java.util.List;

/**
 * @author Vitaliy Gonchar
 * @param <K> - type of filtered value
 */
public class FilterTablePanel<K> extends Panel {

    private final OrienteerDataTable<K, String> dataTable;
    private final FilterForm<IODataFilter<K, String>> filterForm;

    public FilterTablePanel(String id, List<IColumn<K, String>> columns, AbstractFilteredDataProvider<K> provider, int rowsPerRange) {
        super(id);
        filterForm = new FilterForm<IODataFilter<K, String>>("form", provider) {
            @Override
            protected void onSubmit() {
                AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
                target.add(dataTable);
            }
        };
        filterForm.setOutputMarkupPlaceholderTag(true);
        dataTable = new OrienteerDataTable<>("table", columns, provider, rowsPerRange);
        dataTable.addFilterForm(filterForm);
        filterForm.add(dataTable);
        AjaxFallbackButton button = new AjaxFallbackButton("submit", filterForm) {};
        filterForm.setDefaultButton(button);
        filterForm.enableFocusTracking(button);
        filterForm.add(button);
        filterForm.add(dataTable);
        add(filterForm);
    }

    public OrienteerDataTable<K, String> getDataTable() {
        return dataTable;
    }
}
