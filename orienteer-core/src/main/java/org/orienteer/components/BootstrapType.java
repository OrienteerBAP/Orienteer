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
package org.orienteer.components;

public enum BootstrapType {

    DEFAULT("default", "btn-default"),
    PRIMARY("primary", "btn-primary"),
    SUCCESS("success", "btn-success"),
    INFO("info", "btn-info"),
    WARNING("warning", "btn-warning"),
    DANGER("danger", "btn-danger");

    private final String baseCssClass;
    private final String btnCssClass;

    private BootstrapType(String baseCssClass, String btnCssClass) {
        this.baseCssClass = baseCssClass;
        this.btnCssClass = btnCssClass;
    }

    public String getBaseCssClass() {
        return baseCssClass;
    }

    public String getBtnCssClass() {
        return btnCssClass;
    }

}
