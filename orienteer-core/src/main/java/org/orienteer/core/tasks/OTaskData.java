package org.orienteer.core.tasks;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class OTaskData {

	Object innerData;
	
	public OTaskData() {
	}

	public OTaskData(Object innerData) {
		this.innerData = innerData;
	}
	
	public Object getInnerData() {
		return innerData;
	}
	
	@Override
	public String toString() {
		return innerData.toString();
	}

}
