package org.orienteer.core.component.table.filter;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.Iterator;

/**
 * @author Vitaliy Gonchar
 * @param <T> - type of value
 */
public class OrientDbSqlDataProvider<T> extends SortableDataProvider<T, String> implements IDataFilter<String> {

    private OQueryDataProvider<T> dataProvider;
    private IModel<String> sql;

    public OrientDbSqlDataProvider(String sql) {
        this.dataProvider = new OQueryDataProvider<T>(sql);
        this.sql = Model.of(sql);
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        return dataProvider.iterator(first, count);
    }

    @Override
    public long size() {
        return dataProvider.size();
    }

    @Override
    public IModel<T> model(T object) {
        return dataProvider.model(object);
    }


    @Override
    public void updateDataProvider() {
        dataProvider = new OQueryDataProvider<T>(getFilterParam().getObject());
    }

    @Override
    public IVisualizer getVisualizer() {
        return OrienteerWebApplication.lookupApplication()
                .getUIVisualizersRegistry()
                .getComponentFactory(OType.STRING, DataFilter.SQL.getName());
    }

    @Override
    public IModel<String> getFilterParam() {
        return sql;
    }
}
