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

import java.util.regex.Pattern;

import org.apache.wicket.util.time.Duration;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.agilecoders.wicket.webjars.settings.WebjarsSettings;

public class OrienteerWebjarsSettings extends WebjarsSettings {

    @Inject(optional = true)
    public void setReadFromCacheTimeout(@Named("webjars.readFromCacheTimeout") String readFromCacheTimeout) {
        readFromCacheTimeout(Duration.valueOf(readFromCacheTimeout));
    }

    @Inject(optional = true)
    public void setUseCdnResources(@Named("webjars.useCdnResources") boolean useCdnResources) {
        useCdnResources(useCdnResources);
    }

    @Inject(optional = true)
    public void setCdnUrl(@Named("webjars.cdnUrl") String cdnUrl) {
        cdnUrl(cdnUrl);
    }

}
