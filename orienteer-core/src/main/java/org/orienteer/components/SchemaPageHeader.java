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

import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.properties.AbstractLinkViewPanel;
import org.orienteer.web.schema.ListOClassesPage;

public class SchemaPageHeader extends Panel {

    private RepeatingView childRepeatingView;
    private String lastComponentId;

    public SchemaPageHeader(String id) {
        super(id);
        childRepeatingView = new RepeatingView("child");
        add(childRepeatingView);
        addChild(new AbstractLinkViewPanel<Object>(newChildId()) {

            @Override
            protected AbstractLink newLink(String id) {
                return new BookmarkablePageLink<Object>(id, ListOClassesPage.class)
                        .setBody(new ResourceModel("menu.list.class"));
            }
        });
    }

    public SchemaPageHeader addChild(Component component) {
        childRepeatingView.add(component);
        component.add(new AttributeAppender("class", "active") {
            @Override
            public boolean isEnabled(Component component) {
                return super.isEnabled(component) && Objects.equals(lastComponentId, component.getId());
            }
        });
        lastComponentId = component.getId();
        return this;
    }

    public String newChildId() {
        return childRepeatingView.newChildId();
    }

}
