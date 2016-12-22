package org.orienteer.core.tasks;

import java.util.List;

public class OTaskOut {

	Object innerOut;
	
	public OTaskOut(Object innerOut) {
		this.innerOut = innerOut;
	}
	
	public void appendOut(Object newOut) throws Exception{
		if (innerOut instanceof String){
			innerOut = (String)innerOut + (String)newOut;
		}else if(innerOut instanceof List){
			((List)innerOut).add(newOut);
		}else{
			throw new Exception("Unknown innerOut instance "+innerOut.getClass().toString());
		}
	}
	
	protected Object getInnerOut(){
		return innerOut;
	}

}
