package ru.ydn.orienteer.components.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.LoggerOCommandOutputListener;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.db.tool.ODatabaseImport;
import com.orientechnologies.orient.core.metadata.schema.OClass;

public class ImportOSchemaCommand extends AjaxCommand<OClass>
{
	private ModalWindow modal;
	private FileUploadField inputFile;

	public ImportOSchemaCommand(OrienteerDataTable<OClass, ?> table)
	{
		super(new ResourceModel("command.import"), table);
		setIcon(FAIconType.upload);
		setBootstrapType(BootstrapType.SUCCESS);
		modal = new ModalWindow("modal");
		modal.setAutoSize(true);
		add(modal);
		Fragment content = new Fragment(modal.getContentId(), "import", this);
		Form<?> uploadForm = new Form<Object>("uploadForm");
		inputFile = new FileUploadField("inputFile");
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
					ImportOSchemaCommand.this.success(getLocalizer().getString("success.import", ImportOSchemaCommand.this));
				} catch (IOException e)
				{
					ImportOSchemaCommand.this.error(getLocalizer().getString("errors.import.error", ImportOSchemaCommand.this));
				}
				finally
				{
					db.begin();
				}
				modal.close(target);
				ImportOSchemaCommand.this.send(ImportOSchemaCommand.this, Broadcast.BUBBLE, target);
			}
			
		});
		content.add(uploadForm);
		modal.setContent(content);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		modal.show(target);
	}

}
