package org.orienteer.core.component.widget.loader;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.boot.loader.util.artifact.OModuleConfiguration;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public abstract class AbstractOModulesConfigurationsProvider extends SortableDataProvider<OModuleConfiguration, String> implements IOModulesUpdateListener {

    private List<OModuleConfiguration> modulesConfigurations;

    public AbstractOModulesConfigurationsProvider() {
        updateModulesConfigurations();
    }

    @Override
    public Iterator<? extends OModuleConfiguration> iterator(long first, long count) {
        return modulesConfigurations.subList((int) first, (int) (first + count)).iterator();
    }

    @Override
    public long size() {
        return modulesConfigurations.size();
    }

    protected abstract List<OModuleConfiguration> getModulesConfigurations();

    private void sort(List<OModuleConfiguration> modulesConfigurations) {
        Collections.sort(modulesConfigurations);
    }

    @Override
    public IModel<OModuleConfiguration> model(final OModuleConfiguration moduleConfiguration) {
        return Model.of(moduleConfiguration);
    }

    @Override
    public void updateModulesConfigurations() {
        modulesConfigurations = getModulesConfigurations();
        sort(modulesConfigurations);
    }
}
