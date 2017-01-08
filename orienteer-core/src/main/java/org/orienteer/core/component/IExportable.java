package org.orienteer.core.component;

import org.apache.wicket.model.IModel;

/**
 * Interface to mark components which might be exportable within data-tables
 * @param <V> value type
 */
public interface IExportable<V> {
	public IModel<? extends V> getExportableDataModel();
}
