package org.orienteer.core.method;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.behavior.Behavior;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.method.methods.OClassOMethod;
import org.orienteer.core.method.methods.OClassTableOMethod;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * Wrapper interface for OMethod, ClassOMethod annotations and other type of configuration
 *
 */
public interface IMethodConfig extends Serializable{
	public String titleKey();
	public FAIconType icon();
	public BootstrapType bootstrap();
	public boolean changingDisplayMode();
	public boolean changingModel();	
	public int order();
	public String selector();
	public String permission();
	public Class<? extends IMethod> methodClass();
	public Class<? extends IMethod> oClassTableMethodClass();
	public boolean resetSelection();
	
	List<IMethodFilter> filters();
	public List<Class<? extends Behavior>> behaviors();
	
	public String getMethodId();
	
	public void invokeLinkedFunction(IMethodContext dataObject,ODocument doc);
	
}
