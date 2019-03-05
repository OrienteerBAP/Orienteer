package org.orienteer.core.component.table.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.OrienteerHeadersToolbar;
import org.orienteer.core.component.table.filter.IFilterSupportedColumn;
import ru.ydn.wicket.wicketorientdb.model.AbstractFilteredProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.List;

/**
 * Panel for table placing
 * @param <K> - type value in table
 */
public class GenericTablePanel<K> extends Panel {

    private final OrienteerDataTable<K, String> dataTable;


    public GenericTablePanel(String id, List<? extends IColumn<K, String>> columns, OQueryDataProvider<K> provider, int rowsPerRange) {
       this(id, columns, provider, rowsPerRange, true);
    }

    public GenericTablePanel(String id, List<? extends IColumn<K, String>> columns, ISortableDataProvider<K, String> provider, int rowsPerRange) {
        this(id, columns, provider, rowsPerRange, provider instanceof AbstractFilteredProvider && ((AbstractFilteredProvider) provider).isFilterEnable());
    }

    @SuppressWarnings("unchecked")
    private GenericTablePanel(String id, List<? extends IColumn<K, String>> columns,
                              ISortableDataProvider<K, String> provider, int rowsPerRange, boolean filtered) {
        super(id);
        Args.notNull(columns, "columns");
        Args.notNull(provider, "provider");
        setOutputMarkupPlaceholderTag(true);
        dataTable = new OrienteerDataTable<>("table", columns, provider, rowsPerRange);
        if (filtered) {
            FilterForm<OQueryModel<K>> filterForm = createFilterForm("form", (IFilterStateLocator<OQueryModel<K>>) provider);
            filterForm.setOutputMarkupPlaceholderTag(true);
            dataTable.addFilterForm(filterForm);
            filterForm.add(dataTable);
            AjaxFallbackButton button = new AjaxFallbackButton("submit", filterForm) {};
            filterForm.setDefaultButton(button);
            filterForm.enableFocusTracking(button);
            filterForm.add(button);
            filterForm.add(dataTable);
            add(filterForm);
        } else {
            Form form = new Form("form");
            form.add(dataTable);
            form.add(new AjaxFallbackButton("submit", form) {}.setVisible(false));
            add(form);
        }
        add(new EmptyPanel("error").setVisible(false));
    }

    public GenericTablePanel(String id, IModel<String> errorMessage) {
        super(id);
        dataTable = null;
        Form form = new Form("form");
        form.add(new EmptyPanel("table").setVisible(false));
        form.setVisible(false);
        form.add(new AjaxFallbackButton("submit", form) {}.setVisible(false));
        add(new Label("error", errorMessage));
        add(form);
    }

    public OrienteerDataTable<K, String> getDataTable() {
        return dataTable;
    }

    private FilterForm<OQueryModel<K>> createFilterForm(String id, IFilterStateLocator<OQueryModel<K>> provider) {
        return new FilterForm<OQueryModel<K>>(id, provider) {
            @Override
            protected void onSubmit() {
                RequestCycle.get()
                        .find(AjaxRequestTarget.class)
                        .ifPresent(this::onSubmit);
            }

            private void onSubmit(AjaxRequestTarget target) {
                OQueryModel<K> filterState = getStateLocator().getFilterState();
                OrienteerHeadersToolbar<K, String> headersToolbar = dataTable.getHeadersToolbar();
                headersToolbar.clearFilteredColumns();
                updateFilteredColumns(filterState, headersToolbar);
                target.add(dataTable);
            }

            private void updateFilteredColumns(OQueryModel<K> filterState, OrienteerHeadersToolbar<K, String> toolbar) {
                getDataTable().getColumns().stream()
                        .filter(c -> c instanceof IFilterSupportedColumn)
                        .map(c -> (IFilterSupportedColumn) c)
                        .map(IFilterSupportedColumn::getFilterName)
                        .filter(name -> {
                            IFilterCriteriaManager manager = filterState.getFilterCriteriaManager(name);
                            return manager != null && manager.isFilterApply();
                        })
                        .forEach(toolbar::addFilteredColumn);
            }
        };
    }
}
