package org.orienteer.users.model;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.users.module.OrienteerUsersModule;
import org.orienteer.users.repository.OrienteerUserModuleRepository;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * OAuth2 service wrapper.
 * Contains main information for OAuth2
 * @see <a href="https://github.com/scribejava/scribejava">ScribeJava</a>
 */
public class OAuth2Service extends ODocumentWrapper {

    public static final String CLASS_NAME = "OAuth2Service";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Contains client key
     */
    public static final String PROP_API_KEY    = "apiKey";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Contains client API secret
     */
    public static final String PROP_API_SECRET = "apiSecret";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Contains name for {@link IOAuth2Provider}
     */
    public static final String PROP_PROVIDER   = "provider";

    public OAuth2Service() {
        this(CLASS_NAME);
    }

    public OAuth2Service(String iClassName) {
        super(iClassName);
    }

    public OAuth2Service(ODocument iDocument) {
        super(iDocument);
    }

    public String getApiKey() {
        return document.field(PROP_API_KEY);
    }

    public OAuth2Service setApiKey(String key) {
        document.field(PROP_API_KEY, key);
        return this;
    }

    public String getApiSecret() {
        return document.field(PROP_API_SECRET);
    }

    public OAuth2Service setApiSecret(String secret) {
        document.field(PROP_API_SECRET, secret);
        return this;
    }

    public IOAuth2Provider getProvider() {
        String alias = document.field(PROP_PROVIDER);
        return OAuth2Provider.valueOf(alias);
    }

    public OAuth2Service setProvider(IOAuth2Provider provider) {
        document.field(PROP_PROVIDER, provider != null ? provider.getName() : null);
        return this;
    }

    public String getCallback() {
        return DBClosure.sudo(this::getCallback);
    }

    public String getCallback(ODatabaseDocument db) {
        return OrienteerUserModuleRepository.getModuleModel(db)
                .map(OrienteerUsersModule.ModuleModel::getFullOAuth2Callback)
                .orElse(null);
    }

}
