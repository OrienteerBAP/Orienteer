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

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.lang.Objects;
import org.orienteer.services.IOClassIntrospector;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentPageHeader extends GenericPanel<ODocument> {

    @Inject
    private IOClassIntrospector inspector;

    private class GetNavigationPathModel extends LoadableDetachableModel<List<ODocument>> {

        @Override
        protected List<ODocument> load() {
            return inspector.getNavigationPath(ODocumentPageHeader.this.getModelObject(), true);
        }

    }

    public ODocumentPageHeader(String id, IModel<ODocument> model) {
        super(id, model);
        add(new ListView<ODocument>("child", new GetNavigationPathModel()) {

            @Override
            protected void populateItem(ListItem<ODocument> item) {
                item.add(new ODocumentPageLink("link", item.getModel()) {
                    @Override
                    protected boolean isLinkEnabled() {
                        return !Objects.isEqual(getModelObject(), ODocumentPageHeader.this.getModelObject());
                    }
                    }.setDocumentNameAsBody(true));
            }
        });
    }

}
