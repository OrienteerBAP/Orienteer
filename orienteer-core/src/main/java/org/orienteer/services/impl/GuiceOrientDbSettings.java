/**
 * Copyright (C) 2015 Ilia Naryzhny (phantom@ydn.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orienteer.services.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import ru.ydn.wicket.wicketorientdb.OrientDbSettings;

public class GuiceOrientDbSettings extends OrientDbSettings {

    @Inject
    @Override
    public void setDBUrl(@Named("orientdb.url") String url) {
        super.setDBUrl(url);
    }

    @Inject
    @Override
    public void setDBUserName(@Named("orientdb.db.username") String userName) {
        super.setDBUserName(userName);
    }

    @Inject
    @Override
    public void setDBUserPassword(@Named("orientdb.db.password") String password) {
        super.setDBUserPassword(password);
    }

    @Inject
    @Override
    public void setDBInstallatorUserName(@Named("orientdb.db.installator.username") String userName) {
        super.setDBInstallatorUserName(userName);
    }

    @Inject
    @Override
    public void setDBInstallatorUserPassword(@Named("orientdb.db.installator.password") String password) {
        super.setDBInstallatorUserPassword(password);
    }

    @Inject(optional = true)
    @Override
    public void setOrientDBRestApiUrl(@Named("orientdb.rest.url") String orientDbRestApiUrl) {
        super.setOrientDBRestApiUrl(orientDbRestApiUrl);
    }

}
