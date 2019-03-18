package org.orienteer.core.component.table.filter;

/**
 * Interface which indicates that column support filtering
 * @param <T> type class for a filter in this column
 */
public interface IFilterSupportedColumn<T> {

    String getFilterName();
}
