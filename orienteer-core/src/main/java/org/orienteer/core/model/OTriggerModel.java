package org.orienteer.core.model;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.io.Serializable;

/**
 * Model to represent trigger on a document
*/
public class OTriggerModel implements Serializable {
    public static final String TRIGGER = "trigger";
    public static final String FUNCTION = "function";

    private String trigger;
    private String function;
    private ODocument document;

    public OTriggerModel(ODocument document, String trigger, String function) {
        this.document = document;
        this.trigger = trigger;
        this.function = function;
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

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public ODocument getDocument() {
        return document;
    }
}
