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
package org.orienteer.components.commands.modal;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.orienteer.components.commands.ImportOSchemaCommand;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.LoggerOCommandOutputListener;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.tool.ODatabaseImport;

public class ImportDialogPanel extends Panel {

    public ImportDialogPanel(String id, final ModalWindow modal) {
        super(id);
        modal.setMinimalHeight(300);
        Form<?> uploadForm = new Form<Object>("uploadForm");
        final FileUploadField inputFile = new FileUploadField("inputFile");
        uploadForm.add(inputFile);
        uploadForm.add(new AjaxButton("importFile", uploadForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                FileUpload file = inputFile.getFileUpload();
                ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
                db.commit();
                try {
                    InputStream is = file.getInputStream();
                    if (file.getClientFileName().endsWith(".gz") || file.getContentType().contains("gzip")) {
                        is = new GZIPInputStream(is);
                    }
                    ODatabaseImport dbImport = new ODatabaseImport((ODatabaseDocumentInternal) db, is, LoggerOCommandOutputListener.INSTANCE);
                    dbImport.setOptions("-merge=true");
                    dbImport.importDatabase();
                    success(getLocalizer().getString("success.import", this));
                } catch (IOException e) {
                    error(getLocalizer().getString("errors.import.error", this));
                } finally {
                    db.begin();
                }
                modal.close(target);
                send(this, Broadcast.BUBBLE, target);
            }

        });
        add(uploadForm);
    }

}
