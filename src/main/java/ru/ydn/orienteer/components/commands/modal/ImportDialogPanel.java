package ru.ydn.orienteer.components.commands.modal;

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

import ru.ydn.orienteer.components.commands.ImportOSchemaCommand;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.LoggerOCommandOutputListener;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.db.tool.ODatabaseImport;

public class ImportDialogPanel extends Panel
{

	public ImportDialogPanel(String id, final ModalWindow modal)
	{
		super(id);
		Form<?> uploadForm = new Form<Object>("uploadForm");
		final FileUploadField inputFile = new FileUploadField("inputFile");
		uploadForm.add(inputFile);
		uploadForm.add(new AjaxButton("importFile", uploadForm)
		{

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				FileUpload file = inputFile.getFileUpload();
				ODatabaseRecord db = OrientDbWebSession.get().getDatabase();
				db.commit();
				try
				{
					InputStream is = file.getInputStream();
					if(file.getClientFileName().endsWith(".gz") || file.getContentType().contains("gzip"))
					{
						is = new GZIPInputStream(is);
					}
					ODatabaseImport dbImport = new ODatabaseImport((ODatabaseDocument)db, is, LoggerOCommandOutputListener.INSTANCE);
					dbImport.setOptions("-merge=true");
					dbImport.importDatabase();
					success(getLocalizer().getString("success.import", this));
				} catch (IOException e)
				{
					error(getLocalizer().getString("errors.import.error", this));
				}
				finally
				{
					db.begin();
				}
				modal.close(target);
				send(this, Broadcast.BUBBLE, target);
			}
			
		});
		add(uploadForm);
	}
	
}
