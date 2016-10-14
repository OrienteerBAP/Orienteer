package org.orienteer.incident.logger.driver.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ru.asm.utils.incident.logger.core.IData;
import ru.asm.utils.incident.logger.core.ILoggerData;

/**
 * 
 */
public class OrienteerIncidentData implements IData{

	private Map<Integer,String> data;
	private Integer counter = 0;

	public OrienteerIncidentData() {
		data = new HashMap<Integer,String>();
	}
	
	public void applyLoggerData(ILoggerData<?> loggerData) {
		this.data.put(counter++, (String) loggerData.get());
	}

	public String get() {
		// TODO serialize
		
		String result = "";
		for(Entry<Integer, String> entry : data.entrySet()) {
			result = result.concat(entry.getKey().toString())
				.concat("-")
				.concat(entry.getValue())
				.concat("\n");
		}
		return result;
	}

	public void apply(String newData) {
		// TODO unserialize and apply new objects
		
	}
}
