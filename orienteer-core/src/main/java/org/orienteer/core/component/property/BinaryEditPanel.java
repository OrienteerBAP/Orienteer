package org.orienteer.core.component.property;

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
		if(fileUpload!=null)
		{
			setConvertedInput(fileUpload.getBytes());
		}
	}

}
