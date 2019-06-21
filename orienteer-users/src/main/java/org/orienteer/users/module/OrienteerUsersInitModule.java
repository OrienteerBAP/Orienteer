package org.orienteer.users.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.orienteer.users.model.IOAuth2Provider;
import org.orienteer.users.model.OAuth2Provider;

import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

/**
 * Guice init module for 'orienteer-users' module
 */
public class OrienteerUsersInitModule extends AbstractModule {

    /**
     * Provides list of OAuth2 providers with name "orienteer.oauth2.providers".
     * By default returns list of {@link OAuth2Provider} values
     * @return list of OAuth2 providers
     */
    @Provides
    @Named("orienteer.oauth2.providers")
    public List<IOAuth2Provider> provideOAuth2Providers() {
        return Arrays.asList(OAuth2Provider.values());
    }
}
