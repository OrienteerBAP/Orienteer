package org.orienteer.core.component.table.filter.updater;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.io.IClusterable;
import org.orienteer.core.component.table.filter.IDataFilter;

/**
 * @author Vitaliy Gonchar
 * Interface for update Orienteer table filters
 */
public interface IFilterUpdater extends IClusterable {
    void configure(Component componentForUpdate, IDataFilter dataFilter);
    void update(AjaxRequestTarget target);
}
