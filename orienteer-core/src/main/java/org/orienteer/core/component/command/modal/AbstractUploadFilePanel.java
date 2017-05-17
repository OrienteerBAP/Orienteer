package org.orienteer.core.component.command.modal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * For uploading file from modal window
 *
 */

public abstract class AbstractUploadFilePanel extends Panel{
	private static final long serialVersionUID = 1L;

	public AbstractUploadFilePanel(String id, final ModalWindow modal) {
		super(id);
		modal.setMinimalHeight(300);
		Form<?> uploadForm = new Form<Object>("uploadForm");
		final FileUploadField inputFile = new FileUploadField("inputFile");
		uploadForm.add(inputFile);
		uploadForm.add(new AjaxButton("loadFile",getLoadButtonTitle(),uploadForm)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				FileUpload file = inputFile.getFileUpload();
				if (file!=null){
					onLoadFile(file);
	//				error(getLocalizer().getString("errors.load.file.error", this));
					modal.close(target);
					onModalClose(target);
				}
			}
			
		});
		add(uploadForm);		
	}
	public abstract SimpleNamingModel<String> getLoadButtonTitle();
	public abstract void onLoadFile(FileUpload file);
	
	public void onModalClose(AjaxRequestTarget target){
		//may be empty
	};
}
