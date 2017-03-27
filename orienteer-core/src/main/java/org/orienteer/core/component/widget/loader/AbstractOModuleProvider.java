package org.orienteer.core.component.widget.loader;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.boot.loader.util.artifact.OModule;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public abstract class AbstractOModuleProvider extends SortableDataProvider<OModule, String> implements IOModulesUpdateListener {

    private List<OModule> modules;

    public AbstractOModuleProvider() {
        updateModules();
    }

    @Override
    public Iterator<? extends OModule> iterator(long first, long count) {
        return modules.subList((int) first, (int) (first + count)).iterator();
    }

    @Override
    public long size() {
        return modules.size();
    }

    protected abstract List<OModule> getModules();

    private void sort(List<OModule> modules) {
        Collections.sort(modules);
    }

    @Override
    public IModel<OModule> model(final OModule moduleMetadata) {
        return Model.of(moduleMetadata);
    }

    @Override
    public void updateModules() {
        modules = getModules();
        sort(modules);
    }
}
