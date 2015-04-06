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
package org.orienteer.components.properties;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;

public class PasswordsPanel extends FormComponentPanel<String> {

    private PasswordTextField password;
    private PasswordTextField confirmPassword;

    public PasswordsPanel(String id, IModel<String> model) {
        super(id, model);
        password = new PasswordTextField("password", Model.of(""));
        confirmPassword = new PasswordTextField("confirmPassword", Model.of(""));
        add(password, confirmPassword);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        getForm().add(new EqualPasswordInputValidator(password, confirmPassword));
        IModel<String> labelModel = getLabel();
        password.add(new AttributeModifier("placeholder", new StringResourceModel("password.placeholder.enter", labelModel)));
        password.setLabel(labelModel);
        confirmPassword.add(new AttributeModifier("placeholder", new StringResourceModel("password.placeholder.confirm", labelModel)));
        confirmPassword.setLabel(new StringResourceModel("password.confirm.label", labelModel));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        boolean shouldBeRequired = isRequired() && Strings.isEmpty(getModelObject());
        password.setRequired(shouldBeRequired);
        confirmPassword.setRequired(shouldBeRequired);
    }

    @Override
    protected void convertInput() {
        setConvertedInput(password.getConvertedInput());
    }

    @Override
    public void updateModel() {
        if (!Strings.isEmpty(password.getModelObject())) {
            super.updateModel();
        }
    }

}
