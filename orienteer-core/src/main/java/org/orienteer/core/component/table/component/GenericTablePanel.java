package org.orienteer.core.component.table.component;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
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
import org.orienteer.core.component.table.OPropertyValueColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.OrienteerHeadersToolbar;
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
            final FilterForm<OQueryModel<K>> filterForm = new FilterForm<OQueryModel<K>>("form",
                    (IFilterStateLocator<OQueryModel<K>>) provider) {
                @Override
                protected void onSubmit() {
                    AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
                    if(target!=null) {
                        OQueryModel<K> filterState = getStateLocator().getFilterState();
                        OrienteerHeadersToolbar<K, String> headersToolbar = dataTable.getHeadersToolbar();
                        headersToolbar.clearFilteredColumns();
                        for (IColumn<K, String> column : GenericTablePanel.this.getDataTable().getColumns()) {
                            if (column instanceof OPropertyValueColumn) {
                                OPropertyValueColumn propertyValueColumn = (OPropertyValueColumn) column;
                                OProperty property = propertyValueColumn.getCriteryModel().getObject();
                                if (property != null) {
                                    IFilterCriteriaManager manager = filterState.getFilterCriteriaManager(property.getName());
                                    if (manager != null && manager.isFilterApply()) {
                                        headersToolbar.addFilteredColumn(property.getName());
                                    }
                                }
                            }
                        }
                        target.add(dataTable);
                    }
                }
            };
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
}
