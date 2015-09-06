package org.orienteer.core.component.property;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * {@link FormComponentPanel} to edit binary parameters (upload)
 */
public class BinaryEditPanel extends FormComponentPanel<byte[]> {

	protected final CheckBox clear;
	protected FileUploadField fileUploadField;

	public BinaryEditPanel(String id, IModel<byte[]> model) {
		super(id, model);
		fileUploadField = new FileUploadField("data");
		add(fileUploadField);
		fileUploadField.setOutputMarkupId(true);

		clear = new AjaxCheckBox("clear", Model.of(Boolean.FALSE)) {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean shouldClear = clear.getConvertedInput();
				if (shouldClear) {
					fileUploadField.clearInput();
				}

				fileUploadField.setEnabled(!shouldClear);
				target.add(fileUploadField);
			}
		};

		add(clear);
	}

	@Override
	protected void convertInput() {
		if (clear.getConvertedInput())
		{
			setConvertedInput(null);
			return;
		}

		FileUpload fileUpload = fileUploadField.getFileUpload();
		if(fileUpload!=null) {
			setConvertedInput(fileUpload.getBytes());
		}
		else {
			setConvertedInput(getModelObject());
		}
	}
}
