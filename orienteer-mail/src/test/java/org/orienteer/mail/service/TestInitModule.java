package org.orienteer.mail.service;

import com.google.inject.AbstractModule;

public class TestInitModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IOMailService.class).to(OMailServiceTest.class).asEagerSingleton();
    }
}
