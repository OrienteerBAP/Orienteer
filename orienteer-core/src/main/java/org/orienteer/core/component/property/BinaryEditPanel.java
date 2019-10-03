package org.orienteer.core.component.property;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;

import java.util.ArrayList;

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
		nameModel = new PropertyModel<String>(docModel, propModel.getObject().getName()+"$fileName");
	}

	public BinaryEditPanel(String id, IModel<byte[]> model) {
		super(id, model);
		fileUploadField = new FileUploadField("data", Model.ofList(new ArrayList<FileUpload>()));
		add(fileUploadField);
		fileUploadField.setOutputMarkupId(true);

		clear = new CheckBox("clear", Model.of(Boolean.FALSE));

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
