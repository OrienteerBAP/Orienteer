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

import java.io.IOException;
import java.io.OutputStream;

import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.AbstractResource.ResourceResponse;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.orienteer.services.IOClassIntrospector;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class BinaryViewPanel<V> extends GenericPanel<V> {

    private IModel<ODocument> documentModel;
    private IModel<OProperty> propertyModel;
    private IModel<V> valueModel;

    @Inject
    private IOClassIntrospector oClassIntrospector;

    @SuppressWarnings("unchecked")
    public BinaryViewPanel(String id, IModel<ODocument> docModel, IModel<OProperty> propModel, IModel<V> valueModel) {
        super(id, valueModel);
        this.documentModel = docModel;
        this.propertyModel = propModel;
        add(new ResourceLink<byte[]>("data", new AbstractResource() {

            @Override
            protected ResourceResponse newResourceResponse(Attributes attributes) {
                ResourceResponse resourceResponse = new ResourceResponse();
                resourceResponse.setContentType("application/octet-stream");
                String filename = oClassIntrospector.getDocumentName(documentModel.getObject());
                filename += "." + propertyModel.getObject().getName() + ".bin";
                resourceResponse.setFileName(filename);
                resourceResponse.setWriteCallback(new WriteCallback() {
                    @Override
                    public void writeData(Attributes attributes) throws IOException {
                        OutputStream outputStream = attributes.getResponse().getOutputStream();
                        outputStream.write((byte[]) BinaryViewPanel.this.getModelObject());
                    }
                });
                return resourceResponse;
            }
        }));
    }

}
