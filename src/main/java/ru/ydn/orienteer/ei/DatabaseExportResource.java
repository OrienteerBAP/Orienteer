package ru.ydn.orienteer.ei;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.wicket.request.resource.AbstractResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
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
				ODatabaseRecord db = OrientDbWebSession.get().getDatabase();
				ODatabaseExport dbExport = new ODatabaseExport(db, gzipOut, new OCommandOutputListener() {
					
					@Override
					public void onMessage(String iText) {
						LOG.info(iText);
					}
				});
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
