package org.orienteer.core.component.table.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
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
 * @author Vitaliy Gonchar
 * @param <K> - type value in table
 */
public class GenericTablePanel<K> extends Panel {

    private final OrienteerDataTable<K, String> dataTable;

    public GenericTablePanel(String id, List<? extends IColumn<K, String>> columns, AbstractFilteredDataProvider<K> provider, int rowsPerRange) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        Args.notNull(columns, "columns");
        Args.notNull(provider, "provider");
        FilterForm<IODataFilter<K, String>> filterForm = new FilterForm<IODataFilter<K, String>>("form", provider) {
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
        add(new EmptyPanel("error").setVisible(false));
    }

    public GenericTablePanel(String id, IModel<String> errorMessage) {
        super(id);
        dataTable = null;
        Form form = new Form("form");
        form.add(new EmptyPanel("table").setVisible(false));
        form.setVisible(false);
        add(new Label("error", errorMessage));
        add(form);
    }

    public OrienteerDataTable<K, String> getDataTable() {
        return dataTable;
    }
}
