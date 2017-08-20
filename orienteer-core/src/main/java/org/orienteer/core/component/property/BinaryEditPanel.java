package org.orienteer.core.component.property;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link FormComponentPanel} to edit binary parameters (upload)
 */
public class BinaryEditPanel extends FormComponentPanel<byte[]> {
	
	public static final String FILENAME_SUFFIX = "$fileName";

	protected final CheckBox clear;
	protected FileUploadField fileUploadField;
	
	private String tempName; 
	private IModel<String> nameModel;
	
	public BinaryEditPanel(String id, final IModel<ODocument> docModel, final IModel<OProperty> propModel) {
		this(id, docModel, propModel, new DynamicPropertyValueModel<byte[]>(docModel, propModel));
	}
	
	public BinaryEditPanel(String id, final IModel<ODocument> docModel, final IModel<OProperty> propModel, IModel<byte[]> valueModel) {
		this(id, valueModel);
		nameModel = new ODocumentPropertyModel<String>(docModel, propModel.getObject().getName()+"$fileName");
	}

	public BinaryEditPanel(String id, IModel<byte[]> model) {
		super(id, model);
		fileUploadField = new FileUploadField("data", Model.ofList(new ArrayList<FileUpload>()));
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
	public void convertInput() {
		if (clear.getConvertedInput())
		{
			setConvertedInput(null);
			tempName = null;
			return;
		}

		FileUpload fileUpload = fileUploadField.getFileUpload();
		if(fileUpload!=null) {
			setConvertedInput(fileUpload.getBytes());
			tempName = fileUpload.getClientFileName();
		}
		else {
			setConvertedInput(getModelObject());
		}
	}
	
	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		if(nameModel!=null) nameModel.setObject(tempName);
	}
}
