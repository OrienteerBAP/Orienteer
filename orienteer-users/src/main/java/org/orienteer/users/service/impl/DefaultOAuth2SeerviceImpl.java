package org.orienteer.users.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.common.base.Strings;
import com.google.inject.Singleton;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.users.model.*;
import org.orienteer.users.service.IOAuth2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Singleton
public class DefaultOAuth2SeerviceImpl implements IOAuth2Service {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultOAuth2SeerviceImpl.class);

    @Override
    public OAuth2ServiceContext requestAuthorizationUrl(OAuth2Service service, String secretState) {
        OAuth20Service auth20Service = createService(service);
        OAuth2ServiceContext state = new OAuth2ServiceContext();

        state.setService(service)
                .setUsed(false)
                .setState(secretState)
                .setAuthorizationUrl(auth20Service.getAuthorizationUrl(secretState));
        return state;
    }

    @Override
    public boolean authorize(OAuth2Service service, String code) {
        IOAuth2Provider provider = service.getProvider();
        OAuth20Service authService = createService(service);
        OAuth2AccessToken accessToken = getAccessToken(authService, code);

        JsonNode jsonNode = requestProtectedData(authService, accessToken, provider.getProtectedResource());
        LOG.debug("Success authorization for {}: {}", provider.getName(), jsonNode.toString());

        String tmpPassword = UUID.randomUUID().toString();

        String username = DBClosure.sudo(db -> { // need execute from admin for access to user password
            OrienteerUser user = provider.createUser(db, jsonNode);
            user.setPassword(tmpPassword);
            user.save();
            return user.getName();
        });

        return OrienteerWebSession.get().signIn(username, tmpPassword);
    }

    private OAuth20Service createService(OAuth2Service service) {
        IOAuth2Provider provider = service.getProvider();

        ServiceBuilder builder = new ServiceBuilder(service.getApiKey());

        builder.apiSecret(service.getApiSecret())
                .callback(service.getCallback());

        if (!Strings.isNullOrEmpty(provider.getScope())) {
            builder.defaultScope(provider.getScope());
        }

        return builder.build(provider.getInstance());
    }

    private OAuth2AccessToken getAccessToken(OAuth20Service service, String code) {
        try {
            return service.getAccessToken(code);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new IllegalStateException("Can't retrieve access token with code " + code, e);
        }
    }

    private JsonNode requestProtectedData(OAuth20Service service, OAuth2AccessToken token, String url) {
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(token, request);
        try {
            Response response = service.execute(request);
            return new ObjectMapper().readTree(response.getBody());
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new IllegalStateException("Error during request protected data", e);
        }
    }

}
