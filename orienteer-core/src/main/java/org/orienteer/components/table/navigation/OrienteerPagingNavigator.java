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
package org.orienteer.components.table.navigation;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigation;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class OrienteerPagingNavigator extends AjaxPagingNavigator {

    private static class AutoDisableParent extends WebMarkupContainer {

        public AutoDisableParent(String id) {
            super(id);
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            if (!get(0).isEnabledInHierarchy()) {
                tag.append("class", "disabled", " ");
            }
        }
    }

    public OrienteerPagingNavigator(String id, IPageable pageable,
            IPagingLabelProvider labelProvider) {
        super(id, pageable, labelProvider);
    }

    public OrienteerPagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
    }

    @Override
    public MarkupContainer add(Component... childs) {
        for (Component child : childs) {
            String id = child.getId();
            if ("first".equals(id) || "prev".equals(id) || "next".equals(id) || "last".equals(id)) {
                super.add(new AutoDisableParent(id + "Li").add(child));
            } else {
                super.add(child);
            }
        }
        return this;
    }

    @Override
    protected PagingNavigation newNavigation(final String id, final IPageable pageable,
            final IPagingLabelProvider labelProvider) {
        return new AjaxPagingNavigation(id, pageable, labelProvider) {
            @Override
            protected void populateItem(LoopItem loopItem) {
                super.populateItem(loopItem);
                loopItem.add(new AttributeAppender("class", Model.of("active"), " ") {
                    @Override
                    public boolean isEnabled(Component component) {
                        return !((LoopItem) component).get(0).isEnabledInHierarchy();
                    }

                });
            }

        };
    }

}
