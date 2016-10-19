package org.orienteer.incident.logger.driver.component;

import java.util.Date;
import java.util.HashMap;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class OrienteerIncident extends HashMap<String,String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OrienteerIncident() {
		super();
		
	}

	public OrienteerIncident(ODocument doc) {
		
	    put("Application", (String) doc.field("Application"));
	    put("DateTime", (String) doc.field("DateTime"));
	    put("UserName", (String) doc.field("UserName"));
	    put("Message", (String) doc.field("Message"));
	}

}
