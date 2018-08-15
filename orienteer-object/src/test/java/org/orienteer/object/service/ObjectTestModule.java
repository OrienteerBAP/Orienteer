package org.orienteer.object.service;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

public class ObjectTestModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<String> binder = Multibinder.newSetBinder(binder(), String.class, Names.named("orient.model.packages"));
        binder.addBinding().toInstance("org.orienteer.object.model");
    }
}
