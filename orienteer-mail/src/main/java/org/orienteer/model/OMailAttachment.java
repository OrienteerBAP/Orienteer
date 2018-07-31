package org.orienteer.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.apache.tika.Tika;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

public class OMailAttachment extends ODocumentWrapper {

    public static final String CLASS_NAME = "OMailAttachment";

    public static final String PROP_NAME   = "name";
    public static final String PROP_DATA   = "data";
    public static final String PROP_STATIC = "static";

    public OMailAttachment() {
        super(CLASS_NAME);
    }

    public OMailAttachment(ODocument iDocument) {
        super(iDocument);
    }

    public String getName() {
        return document.field(PROP_NAME);
    }

    public OMailAttachment setName(String name) {
        document.field(PROP_NAME, name);
        return this;
    }

    public byte[] getData() {
        byte [] data = document.field(PROP_DATA);
        return data != null ? data : new byte[0];
    }

    public OMailAttachment setData(byte[] data) {
        document.field(PROP_DATA, data);
        return this;
    }

    public boolean isStatic() {
        return document.field(PROP_STATIC);
    }

    public OMailAttachment setStatic(boolean isStatic) {
        document.field(PROP_STATIC, isStatic);
        return this;
    }

    public DataSource toDataSource() {
        byte [] data = getData();
        ByteArrayDataSource dataSource = new ByteArrayDataSource(data, new Tika().detect(data));
        dataSource.setName(getName());
        return dataSource;
    }
}
