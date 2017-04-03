package org.orienteer.core.component.table.filter;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;
import org.orienteer.core.component.visualizer.IVisualizer;

/**
 * @author Vitaliy Gonchar
 * @param <F> type of filter parameter
 */
public interface IDataFilter<F> extends IClusterable {
    void updateDataProvider();
    IVisualizer getVisualizer();
    IModel<F> getFilterParam();
}
