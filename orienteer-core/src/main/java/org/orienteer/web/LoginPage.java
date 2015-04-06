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

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/login")
public class LoginPage extends BasePage<Object> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public LoginPage() {
        super();
        SignInPanel signInPanel = new SignInPanel("signInPanel", true);
        signInPanel.setRememberMe(false);
        add(signInPanel);
    }

    @Override
    public IModel<String> getTitleModel() {
        return new ResourceModel("login.title");
    }

}
