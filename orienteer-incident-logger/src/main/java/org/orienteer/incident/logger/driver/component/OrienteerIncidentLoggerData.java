package org.orienteer.incident.logger.driver.component;

import java.util.ArrayList;
import java.util.List;

import ru.asm.utils.incident.logger.core.ILoggerData;

/**
 * 
 */
public class OrienteerIncidentLoggerData implements ILoggerData<List<OrienteerIncident>>{

	List<OrienteerIncident> data;
	boolean makeNew = false;

	public OrienteerIncidentLoggerData() {
		data = new ArrayList<OrienteerIncident>();
		makeNew=true;
	}

	public List<OrienteerIncident> get(){
		return data;
	}

	public void clear() {
		data.clear();
		makeNew=true;
	}

	public void set(String name, String value) {
		if (makeNew){
			data.add(new OrienteerIncident());
			makeNew=false;
		}
		data.get(data.size()-1).put(name, value);
	}

	public void end() {
		makeNew=true;
	}
}
