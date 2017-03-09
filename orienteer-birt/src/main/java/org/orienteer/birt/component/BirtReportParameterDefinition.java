package org.orienteer.birt.component;

import java.io.Serializable;

import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.impl.ScalarParameterDefn;

public class BirtReportParameterDefinition implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String typeName;
	private String name;
	private String displayName;
	private String promptText;
	private String defaultValue;
	private int controlType;
	

	public BirtReportParameterDefinition(IParameterDefnBase iParameterDefnBase) {
		if (iParameterDefnBase instanceof ScalarParameterDefn){
			ScalarParameterDefn scalarParameterDefn = (ScalarParameterDefn) iParameterDefnBase;
			typeName = iParameterDefnBase.getTypeName();
			name = iParameterDefnBase.getName();
			displayName = iParameterDefnBase.getDisplayName();
			promptText = iParameterDefnBase.getPromptText();
			controlType = scalarParameterDefn.getControlType();
			defaultValue = scalarParameterDefn.getDefaultValue();
		}
	}


	public String getTypeName() {
		return typeName;
	}


	public String getName() {
		return name;
	}


	public String getDisplayName() {
		return displayName;
	}


	public String getPromptText() {
		return promptText;
	}


	public String getDefaultValue() {
		return defaultValue;
	}


	public int getControlType() {
		return controlType;
	}
	
	

}
