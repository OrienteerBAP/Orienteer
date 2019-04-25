package org.orienteer.users.service;

import com.google.inject.ImplementedBy;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.model.OAuth2ServiceContext;
import org.orienteer.users.service.impl.DefaultOAuth2ServiceImpl;

/**
 * OAuth2 service for login and register users throughout social networks
 */
@ImplementedBy(DefaultOAuth2ServiceImpl.class)
public interface IOAuth2Service {

    /**
     * Request authorization url
     * @see <a href="https://github.com/scribejava/scribejava">ScribeJava</a>
     * @param service service which need use for request
     * @param secretState secret OAuth2 state
     * @return {@link OAuth2ServiceContext} context for user login
     */
    OAuth2ServiceContext requestAuthorizationUrl(OAuth2Service service, String secretState);

    /**
     * Authorize user by given service and code
     * @param service service for authorize user
     * @param code OAuth2 code which was received from OAuth2 server
     * @return true if authorized
     */
    boolean authorize(OAuth2Service service, String code);

}
