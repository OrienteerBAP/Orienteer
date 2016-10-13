package org.orienteer.incident.logger.driver.component;

import ru.asm.utils.incident.logger.core.ILoggerData;

/**
 * 
 */
public class OrienteerIncidentLoggerData implements ILoggerData<String>{

	String data;

	public OrienteerIncidentLoggerData() {
		data = "";
	}

	public String get(){
		return data;
	}

	public void clear() {
		data = "";
	}

	public void set(String name, String value) {
		this.data = this.data.concat(name+":"+value+"\n");
	}

	public void end() {
		this.data = this.data.concat("\n\n");
	}
}
