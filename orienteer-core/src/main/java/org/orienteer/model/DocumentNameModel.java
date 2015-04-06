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
package org.orienteer.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.orienteer.OrienteerWebApplication;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class DocumentNameModel extends LoadableDetachableModel<String> {

    private static final long serialVersionUID = 1L;
    private IModel<ODocument> documentModel;

    public DocumentNameModel(IModel<ODocument> documentModel) {
        this.documentModel = documentModel;
    }

    @Override
    protected String load() {
        return OrienteerWebApplication.get().getOClassIntrospector().getDocumentName(documentModel.getObject());
    }

    @Override
    public void detach() {
        documentModel.detach();
    }

}
