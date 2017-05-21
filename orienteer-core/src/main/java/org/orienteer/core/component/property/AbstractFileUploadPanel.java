package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Panel for upload file
 */
public abstract class AbstractFileUploadPanel extends Panel {
    public AbstractFileUploadPanel(String id) {
        super(id);
        FileUploadField uploadField = new FileUploadField("uploadFile");
        configureFileUploadField(uploadField);
        add(uploadField);
    }

    /**
     * Configure behavior of file upload field
     * @param uploadField - field for configure
     */
    protected abstract void configureFileUploadField(FileUploadField uploadField);

}
