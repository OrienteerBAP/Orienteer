package org.orienteer.users.model;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.users.module.OrienteerUsersModule;
import org.orienteer.users.util.OUsersDbUtils;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

public class OAuth2Service extends ODocumentWrapper {

    public static final String CLASS_NAME = "OAuth2Service";

    public static final String PROP_API_KEY    = "apiKey";
    public static final String PROP_API_SECRET = "apiSecret";
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
        return OUsersDbUtils.getModuleModel(db)
                .map(OrienteerUsersModule.ModuleModel::getFullOAuth2Callback)
                .orElse(null);
    }

}
