package org.orienteer.core.component.property;

import org.apache.tika.Tika;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.model.IModel;

/**
 * {@link FormComponentPanel} to upload images
 */
public class ImageEditPanel extends BinaryEditPanel {
    public ImageEditPanel(String id, IModel<byte[]> model) {
        super(id, model);
    }

    @Override
    public void validate() {
        super.validate();
        FileUpload fileUpload = fileUploadField.getFileUpload();
        if(fileUpload!=null) {
            byte[] bytes = fileUpload.getBytes();
            boolean isImage = new Tika().detect(bytes).startsWith("image/");
            if (!isImage) {
                error(getString("errors.wrong.image.uploaded"));
            }
        }
    }
}
