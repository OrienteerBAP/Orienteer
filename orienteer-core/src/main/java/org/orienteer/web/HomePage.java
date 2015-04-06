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
package org.orienteer.web;

import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.wicketstuff.annotation.mount.MountPath;

import com.orientechnologies.orient.core.record.impl.ODocument;

@MountPath("/home")
public class HomePage extends BasePage {

    public HomePage() {
        ODocument perspective = getPerspective();
        String homeUrl = perspective.field("homeUrl");
        throw new RedirectToUrlException(homeUrl);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }
}
