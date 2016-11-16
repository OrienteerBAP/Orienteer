package org.orienteer.bpm.component.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.util.io.IClusterable;
import org.camunda.bpm.engine.ProcessEngine;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Utility class to parse formkey 
 */
public class FormKey implements IClusterable{
	
	public static final String ORIENTEER_PREFIX = "orienteer:";
	
	private static final Pattern FORM_KEY_PATTERN = Pattern.compile("orienteer:((\\w+):)?(.*)", Pattern.CASE_INSENSITIVE);
	
	private String schemaClassName;
	private String variableName;
	private ORID rid;
	
	private FormKey() {
		
	}
	
	public static FormKey parse(String formKey) {
		FormKey ret = new FormKey();
		if(formKey!=null) {
			Matcher m = FORM_KEY_PATTERN.matcher(formKey);
			if(m.matches()){
				ret.schemaClassName = m.group(2);
				String val = m.group(3);
				if(ORecordId.isA(val)) {
					ret.rid = new ORecordId(val);
				} else {
					ret.variableName = val;
				}
			}
		}
		return ret;
	}
	
	public String getSchemClassName() {
		return schemaClassName;
	}

	public String getVariableName() {
		return variableName;
	}

	public ORID getRid() {
		return rid;
	}
	
	public ODocument calculateODocument(ProcessEngine processEngine, String taskId) {
		if(rid!=null) return (ODocument)rid.getRecord();
		if(variableName!=null) {
			Object recording = processEngine.getTaskService().getVariable(taskId, variableName);
			if(recording!=null) {
				return new ORecordId(recording.toString()).getRecord();
			}
		}
		if(schemaClassName!=null) {
			return new ODocument(schemaClassName);
		}
		return null;
	}

	public boolean isValid() {
		return variableName!=null || rid!=null;
	}
	
	@Override
	public String toString() {
		return "orienteer:"+(schemaClassName!=null?schemaClassName+":":"")+(variableName!=null?variableName:(rid!=null?rid.toString():""));
	}

}
