package org.orienteer.inclogger.client;

import java.util.ArrayList;
import java.util.List;

import org.orienteer.logger.core.interfaces.ILoggerData;

/**
 * 
 */
public class OIncidentLoggerData implements ILoggerData<List<OIncident>>{

	List<OIncident> data;
	boolean makeNew = false;

	public OIncidentLoggerData() {
		data = new ArrayList<OIncident>();
		makeNew=true;
	}

	public List<OIncident> get(){
		return data;
	}

	public void clear() {
		data.clear();
		makeNew=true;
	}

	public OIncidentLoggerData set(String name, String value) {
		if (makeNew){
			data.add(new OIncident());
			makeNew=false;
		}
		data.get(data.size()-1).put(name, value);
		return this;
	}

	public void end() {
		makeNew=true;
	}
}
