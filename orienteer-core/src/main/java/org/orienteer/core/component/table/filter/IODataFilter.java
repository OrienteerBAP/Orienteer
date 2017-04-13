package org.orienteer.core.component.table.filter;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;

/**
 * @author Vitaliy Gonchar
 * Only for use with OrientDB
 * @param <T> type of filtered objects OClass or ODocument
 * @param <F> type of filtered properties String or OProperty
 */
public interface IODataFilter<T, F> extends IClusterable {
    String getSql();
    String createNewSql();
    IModel<?> getFilteredValueByProperty(F filteredProperty);
}
