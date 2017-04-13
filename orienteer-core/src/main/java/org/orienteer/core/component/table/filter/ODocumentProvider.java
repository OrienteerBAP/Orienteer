package org.orienteer.core.component.table.filter;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.Iterator;

/**
 * @author Vitaliy Gonchar
 */
public class ODocumentProvider extends SortableDataProvider<ODocument, String>
        implements IFilterStateLocator<IODataFilter<ODocument, String>> {

    private OQueryDataProvider<ODocument> queryProvider;
    private IODataFilter<ODocument, String> dataFilter;
    private String currentSql;

    public ODocumentProvider(String sql) {
        this.queryProvider = new OQueryDataProvider<>(sql);
        this.currentSql = sql;
    }

    @Override
    public Iterator<? extends ODocument> iterator(long first, long count) {
        changeSql();
        return queryProvider.iterator(first, count);
    }

    @Override
    public long size() {
        changeSql();
        return queryProvider.size();
    }

    private void changeSql() {
        if (dataFilter != null) {
            currentSql = dataFilter.createNewSql();
            queryProvider = new OQueryDataProvider<>(currentSql);
        }
    }

    @Override
    public IModel<ODocument> model(ODocument document) {
        return Model.of(document);
    }

    @Override
    public IODataFilter<ODocument, String> getFilterState() {
        return dataFilter;
    }

    @Override
    public void setFilterState(IODataFilter<ODocument, String> dataFilter) {
        this.dataFilter = dataFilter;
    }
}
