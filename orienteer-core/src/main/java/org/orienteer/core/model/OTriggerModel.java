package org.orienteer.core.model;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.io.Serializable;

/**
 * Model to represent trigger on a document
*/
public class OTriggerModel implements Serializable {
    public static final String TRIGGER = "trigger";
    public static final String FUNCTION = "functionName";

    private String trigger;
    private String functionName;
    private ODocument document;

    public OTriggerModel(ODocument document, String trigger, String functionName) {
        this.document = document;
        this.trigger = trigger;
        this.functionName = functionName;
    }

    public OTriggerModel(ODocument document) {
        this.document = document;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String function) {
        this.functionName = function;
    }

    public ODocument getDocument() {
        return document;
    }
}
