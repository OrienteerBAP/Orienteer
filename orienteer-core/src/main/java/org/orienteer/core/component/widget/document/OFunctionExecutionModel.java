package org.orienteer.core.component.widget.document;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Model for executing a function
*/
public class OFunctionExecutionModel implements Serializable {
    public static final String NAME = "name";
    public static final String PARAMETERS = "parameters";
    public static final String RESULT = "result";

    private String name;
    private Map<String, String> parameters = new LinkedHashMap<>();
    private String result = "";
    private ODocument document;

    public OFunctionExecutionModel(ODocument document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ODocument getDocument() {
        return document;
    }
}
