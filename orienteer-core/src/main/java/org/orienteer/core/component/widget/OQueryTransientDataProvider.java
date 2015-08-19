package org.orienteer.core.component.widget;

import com.google.common.collect.Iterators;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * //todo
 *  @param <K> todo
 */
public abstract class OQueryTransientDataProvider<K> extends SortableDataProvider<K, String> {

    private OQueryDataProvider<K> queryDataProvider;
    private List<K> transientData;

    public OQueryTransientDataProvider(OQueryDataProvider<K> queryDataProvider) {
        this.queryDataProvider = queryDataProvider;
        this.transientData = new ArrayList<K>();
    }

    @Override
    public Iterator<? extends K> iterator(long first, long count) {
        return Iterators.concat(queryDataProvider.iterator(first, count - transientData.size()),
                transientData.iterator());
    }

    @Override
    public long size() {
        return queryDataProvider.size() + transientData.size();
    }

    public void addTransientData(K data) {
        transientData.add(data);
    }

    public boolean removeTransientData(K object) {
        return transientData.remove(object);
    }
}
