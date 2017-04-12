package org.orienteer.core.component.table.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;
import org.orienteer.core.component.visualizer.IVisualizer;

import java.util.Map;

/**
 * @author Vitaliy Gonchar
 */
public interface IDataFilter extends IClusterable {
    Map<IModel<OProperty>, IModel<?>> getDataFilters();
    void updateDataProvider();
    IModel<OProperty> getOPropertyModelByName(String name);
    IVisualizer getVisualizer();
}
