package org.orienteer.core.component.widget.loader;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Provider for Orienteer modules in system.
 */
public abstract class AbstractOArtifactsProvider extends SortableDataProvider<OArtifact, String> implements IOArtifactsUpdateListener {

    private List<OArtifact> oArtifacts;

    public AbstractOArtifactsProvider() {
        updateOArtifacts();
    }

    @Override
    public Iterator<? extends OArtifact> iterator(long first, long count) {
        return oArtifacts.subList((int) first, (int) (first + count)).iterator();
    }

    @Override
    public long size() {
        return oArtifacts.size();
    }

    protected abstract List<OArtifact> getOArtifacts();

    private void sort(List<OArtifact> oArtifacts) {
        Collections.sort(oArtifacts);
    }

    @Override
    public IModel<OArtifact> model(final OArtifact oArtifact) {
        return Model.of(oArtifact);
    }

    @Override
    public void updateOArtifacts() {
        oArtifacts = getOArtifacts();
        sort(oArtifacts);
    }
}
