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
public interface IMethodDefinition extends Serializable{
	public String getTitleKey();
	public FAIconType getIcon();
	public BootstrapType getBootstrapType();
	public boolean isChangingDisplayMode();
	public boolean isChangingModel();	
	public int getOrder();
	public String getSelector();
	public String getPermission();
	public Class<? extends IMethod> getIMethodClass();
	public Class<? extends IMethod> getTableIMethodClass();
	public boolean isResetSelection();
	
	List<IMethodFilter> getFilters();
	public List<Class<? extends Behavior>> getBehaviors();
	
	public String getMethodId();
	
	public boolean isSupportedMethod(IMethodContext context);
	
	public IMethod getMethod(IMethodContext context);
	
	public void invokeLinkedFunction(IMethodContext context,ODocument doc);
	
}
