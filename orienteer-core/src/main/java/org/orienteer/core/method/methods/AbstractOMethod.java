package org.orienteer.core.method.methods;

import java.io.Serializable;

import org.apache.wicket.util.string.Strings;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodEnvironmentData;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * 
 * Base class for OMethods
 *
 */
public abstract class AbstractOMethod implements Serializable,IMethod{
	private static final long serialVersionUID = 1L;

	private IMethodEnvironmentData envData;
	private String id;
	
	@Override
	public void methodInit(String id, IMethodEnvironmentData envData) {
		this.envData = envData;
		this.id = id;		
	}

	protected SimpleNamingModel<String> getTitleModel(){
		if (!Strings.isEmpty(getTitleKey())){
			return new SimpleNamingModel<String>(getTitleKey());			
		}
		return new SimpleNamingModel<String>(id);
	}
	
	protected IMethodEnvironmentData getEnvData() {
		return envData;
	}

	protected String getId() {
		return id;
	}
	
	protected abstract String getTitleKey();

}
