package org.orienteer.devutils.component;

import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

import ru.ydn.wicket.wicketconsole.IScriptEngineInterlayerResult;
import ru.ydn.wicket.wicketconsole.IScriptEngineInterlayerResultRenderer;

/**
 * Result object for {@link ODBScriptEngineInterlayer}
 *
 */
public class ODBScriptEngineInterlayerResult implements IScriptEngineInterlayerResult {

	private String error; 
	private String out;
	private transient Object returnedObject;
	
	public ODBScriptEngineInterlayerResult() {
	}

	protected void setOut(String out) {
		this.out = out;
	}

	@Override
	public String getOut() {
		return out;
	}

	@Override
	public String getError() {
		return error;
	}
	
	protected void setError(String error) {
		this.error = error;
	}

	@Override
	public Object getReturnedObject() {
		return returnedObject;
	}
	
	protected void setReturnedObject(Object returnedObject) {
		this.returnedObject = returnedObject;
	}

	@Override
	public void onUpdate() {
		if (returnedObject!=null){
			out = returnedObject.toString();
		}
	}
}
