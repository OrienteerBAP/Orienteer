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
import ru.ydn.wicket.wicketorientdb.filter.AbstractFilteredDataProvider;
import ru.ydn.wicket.wicketorientdb.filter.IODataFilter;

import java.util.List;

/**
 * Panel for table placing
 * @param <K> - type value in table
 */
public class GenericTablePanel<K> extends Panel {

    private final OrienteerDataTable<K, String> dataTable;



    public GenericTablePanel(String id, List<? extends IColumn<K, String>> columns, AbstractFilteredDataProvider<K> provider, int rowsPerRange) {
       this(id, columns, provider, rowsPerRange, true);
    }

    public GenericTablePanel(String id, List<? extends IColumn<K, String>> columns, ISortableDataProvider<K, String> provider, int rowsPerRange) {
        this(id, columns, provider, rowsPerRange, provider instanceof AbstractFilteredDataProvider);
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
            FilterForm<IODataFilter<K, String>> filterForm = new FilterForm<IODataFilter<K, String>>("form", (IFilterStateLocator<IODataFilter<K, String>>) provider) {
                @Override
                protected void onSubmit() {
                    AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
                    if(target!=null) target.add(dataTable);
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
