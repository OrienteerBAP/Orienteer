package org.orienteer.core.component.widget.loader;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public abstract class AbstractOArtifactsProvider extends SortableDataProvider<OArtifact, String> implements IOModulesUpdateListener {

    private List<OArtifact> modulesConfigurations;

    public AbstractOArtifactsProvider() {
        updateModulesConfigurations();
    }

    @Override
    public Iterator<? extends OArtifact> iterator(long first, long count) {
        return modulesConfigurations.subList((int) first, (int) (first + count)).iterator();
    }

    @Override
    public long size() {
        return modulesConfigurations.size();
    }

    protected abstract List<OArtifact> getModulesConfigurations();

    private void sort(List<OArtifact> modulesConfigurations) {
        Collections.sort(modulesConfigurations);
    }

    @Override
    public IModel<OArtifact> model(final OArtifact moduleConfiguration) {
        return Model.of(moduleConfiguration);
    }

    @Override
    public void updateModulesConfigurations() {
        modulesConfigurations = getModulesConfigurations();
        sort(modulesConfigurations);
    }
}
