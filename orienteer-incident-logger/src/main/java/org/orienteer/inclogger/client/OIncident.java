package org.orienteer.inclogger.client;

import java.util.HashMap;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Inner representation of incident for Orienteer
 *
 */
public class OIncident extends HashMap<String,String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OIncident() {
		super();
		
	}

	public OIncident(ODocument doc) {
		
	    put("application", (String) doc.field("application"));
	    put("dateTime", (String) doc.field("dateTime"));
	    put("userName", (String) doc.field("userName"));
	    put("message", (String) doc.field("message"));
	    put("stackTrace", (String) doc.field("stackTrace"));
	}

}
