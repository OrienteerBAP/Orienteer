package org.orienteer.core.pageStore;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Wrapper for Wicket data which need store in OrientDB
 */
public class OWicketData extends ODocumentWrapper {

    public static final String CLASS_NAME = "OWicketData";

    public static final String PROP_ID         = "id";
    public static final String PROP_SESSION_ID = "sessionId";
    public static final String PROP_DATA       = "data";

    public OWicketData() {
        super(CLASS_NAME);
    }

    public OWicketData(ODocument iDocument) {
        super(iDocument);
    }

    public OWicketData(int id, String sessionId, byte[] data) {
        this();
        setId(id);
        setSessionId(sessionId);
        setData(data);
    }

    public int getId() {
        Integer integer = document.field(PROP_ID);
        return integer != null ? integer : -1;
    }

    public OWicketData setId(int id) {
        document.field(PROP_ID, id);
        return this;
    }

    public String getSessionId() {
        return document.field(PROP_SESSION_ID);
    }

    public OWicketData setSessionId(String sessionId) {
        document.field(PROP_SESSION_ID, sessionId);
        return this;
    }

    public byte[] getData() {
        byte [] data = document.field(PROP_DATA);
        return data != null ? data : new byte[0];
    }

    public OWicketData setData(byte [] data) {
        document.field(PROP_DATA, data);
        return this;
    }
}
