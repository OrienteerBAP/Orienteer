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
package org.orienteer.ei;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.wicket.request.resource.AbstractResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.LoggerOCommandOutputListener;

import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.tool.ODatabaseExport;

public class DatabaseExportResource extends AbstractResource {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseExportResource.class);

    @Override
    protected ResourceResponse newResourceResponse(Attributes attrs) {
        ResourceResponse resourceResponse = new ResourceResponse();
        resourceResponse.setContentType("application/x-gzip");
        resourceResponse.setFileName("export.gz");
        resourceResponse.setWriteCallback(new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) throws IOException {
                OutputStream out = attributes.getResponse().getOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(out);
                ODatabaseDocumentInternal db = (ODatabaseDocumentInternal) OrientDbWebSession.get().getDatabase();
                ODatabaseExport dbExport = new ODatabaseExport(db, gzipOut, LoggerOCommandOutputListener.INSTANCE);
                configureODatabaseExport(dbExport);
                dbExport.exportDatabase();
            }
        });
        return resourceResponse;
    }

    protected void configureODatabaseExport(ODatabaseExport dbExport) {

    }

}
