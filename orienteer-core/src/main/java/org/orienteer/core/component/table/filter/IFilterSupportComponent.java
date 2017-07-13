package org.orienteer.core.component.table.filter;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

/**
 * Interface to mark components which supports filtering
 * @param <K> type of values in table
 */
public interface IFilterSupportComponent<K> {
    void addFilterForm(FilterForm<OQueryModel<K>> filterForm);
}
