package org.orienteer.core.wicket.pageStore;

import org.apache.wicket.page.IManageablePage;
import org.apache.wicket.pageStore.AbstractCachingPageStore;
import org.apache.wicket.pageStore.IDataStore;
import org.apache.wicket.serialize.ISerializer;

/**
 * Implementation of {@link AbstractCachingPageStore} which uses {@link HazelcastPagesCache} for store pages
 */
public class HazelcastPageStore extends AbstractCachingPageStore<IManageablePage> {
    /**
     * Constructor.
     *
     * @param pageSerializer The serializer that will convert pages to/from byte[]
     * @param dataStore      The third level page cache
     */
    public HazelcastPageStore(ISerializer pageSerializer, IDataStore dataStore) {
        super(pageSerializer, dataStore, new HazelcastPagesCache());
    }

    @Override
    public IManageablePage convertToPage(Object object) {
        if (object == null)
        {
            return null;
        }
        else if (object instanceof IManageablePage)
        {
            return (IManageablePage)object;
        }

        String type = object.getClass().getName();
        throw new IllegalArgumentException("Unknown object type: " + type);
    }

    @Override
    public boolean canBeAsynchronous() {
        return false;
    }
}
