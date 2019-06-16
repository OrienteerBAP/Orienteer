package org.orienteer.logger.server.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.orienteer.core.service.OverrideModule;
import org.orienteer.mail.service.IOMailService;

@OverrideModule
public class TestInitModule extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();

        bind(IOMailService.class).to(OTestLoggerMailService.class).in(Singleton.class);
    }
}
