package org.orienteer.core.resource;

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

public class DatabaseExportResource extends AbstractResource
{
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
				ODatabaseDocumentInternal db = (ODatabaseDocumentInternal)OrientDbWebSession.get().getDatabase();
				ODatabaseExport dbExport = new ODatabaseExport(db, gzipOut, LoggerOCommandOutputListener.INSTANCE);
				configureODatabaseExport(dbExport);
				dbExport.exportDatabase();
			}
		});
		return resourceResponse;
	}
	
	protected void configureODatabaseExport(ODatabaseExport dbExport)
	{
		
	}

}
