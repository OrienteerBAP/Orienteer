package org.orienteer.users.service;

import com.google.inject.ImplementedBy;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.model.OAuth2ServiceContext;
import org.orienteer.users.service.impl.DefaultOAuth2SeerviceImpl;

@ImplementedBy(DefaultOAuth2SeerviceImpl.class)
public interface IOAuth2Service {

    OAuth2ServiceContext requestAuthorizationUrl(OAuth2Service service, String secretState);

    boolean authorize(OAuth2Service service, String code);

}
