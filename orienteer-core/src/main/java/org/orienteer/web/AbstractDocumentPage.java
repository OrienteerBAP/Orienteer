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

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class AbstractDocumentPage extends OrienteerBasePage<ODocument> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AbstractDocumentPage() {
        super();
    }

    public AbstractDocumentPage(IModel<ODocument> model) {
        super(model);
    }

    public AbstractDocumentPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected IModel<ODocument> resolveByPageParameters(PageParameters parameters) {
        String rid = parameters.get("rid").toOptionalString();
        if (rid != null) {
            try {
                return new ODocumentModel(new ORecordId(rid));
            } catch (IllegalArgumentException e) {
                //NOP Support of case with wrong rid
            }
        }
        return new ODocumentModel((ODocument) null);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        ODocument doc = getDocument();
        if (doc == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        } else if (Strings.isEmpty(doc.getClassName()) && doc.getIdentity().isValid()) {
            //Support of case when metadata was changed in parallel
            getDatabase().reload();
            if (Strings.isEmpty(doc.getClassName())) {
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public IModel<ODocument> getDocumentModel() {
        return (IModel<ODocument>) getDefaultModel();
    }

    public ODocument getDocument() {
        return getDocumentModel().getObject();
    }

}
