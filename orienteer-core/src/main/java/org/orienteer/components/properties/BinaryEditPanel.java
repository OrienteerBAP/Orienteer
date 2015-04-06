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

import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;

public class BinaryEditPanel extends FormComponentPanel<byte[]> {

    private FileUploadField fileUploadField;

    public BinaryEditPanel(String id, IModel<byte[]> model) {
        super(id, model);
        fileUploadField = new FileUploadField("data");
        add(fileUploadField);
    }

    @Override
    protected void convertInput() {
        FileUpload fileUpload = fileUploadField.getFileUpload();
        if (fileUpload != null) {
            setConvertedInput(fileUpload.getBytes());
        }
    }

}
