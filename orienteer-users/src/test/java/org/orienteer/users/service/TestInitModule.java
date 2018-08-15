package org.orienteer.users.service;

import com.google.inject.AbstractModule;

public class TestInitModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IOrienteerUsersService.class).to(TestOrienteerUserService.class).asEagerSingleton();
    }
}
