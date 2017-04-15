package org.orienteer.core.component.table.filter;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import ru.ydn.wicket.wicketorientdb.filter.IODataFilter;

/**
 * @author Vitaliy Gonchar
 * @param <T> type of values in table
 * @param <S> type of sort
 */
public interface IFilterSupportComponent<T, S> {
    void addFilterForm(FilterForm<IODataFilter<T, S>> filterForm);
}
