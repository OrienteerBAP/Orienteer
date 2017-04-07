package org.orienteer.core.component.table.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;
import org.orienteer.core.component.visualizer.IVisualizer;

import java.util.Map;

/**
 * @author Vitaliy Gonchar
 * @param <F> type of filter parameter
 */
public interface IDataFilter<F> extends IClusterable {
    void updateDataProvider(Map<OProperty, IModel<?>> propertyFilters);
    IVisualizer getVisualizer();
}
