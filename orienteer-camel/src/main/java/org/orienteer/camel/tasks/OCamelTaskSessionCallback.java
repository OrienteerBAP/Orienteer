package org.orienteer.camel.tasks;

import org.apache.camel.CamelContext;
import org.orienteer.core.tasks.ITaskSessionCallback;

public class OCamelTaskSessionCallback implements ITaskSessionCallback{

	private volatile CamelContext context;
	
	public OCamelTaskSessionCallback(CamelContext context) {
		this.context=context;
	}

	@Override
	public void stop() throws Exception{
		if(context!=null){
			context.stop();
		}
	}

}
