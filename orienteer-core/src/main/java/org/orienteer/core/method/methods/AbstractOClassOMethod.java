package org.orienteer.core.method.methods;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.IClassMethod;
import org.orienteer.core.method.IMethodEnvironmentData;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * Base class for OMethods linked to OClass.
 * Using {@link ClassOMethod} annotation
 *
 */

public abstract class AbstractOClassOMethod extends AbstractOMethod implements IClassMethod{

	private static final long serialVersionUID = 1L;

	private String javaMethodName;
	private String javaClassName;
	private ClassOMethod annotation;

	@Override
	public void initOClassMethod(Method javaMethod) {
		this.javaMethodName = javaMethod.getName();
		this.javaClassName = javaMethod.getDeclaringClass().getName();
		this.annotation = javaMethod.getAnnotation(ClassOMethod.class);
		
	}

	protected void invoke(){
		invoke(null);
	}
	
	protected void invoke(ODocument doc){
		
		try {
			Constructor<?> constructor=null;
			try {
				constructor = Class.forName(javaClassName).getConstructor(ODocument.class);
			} catch (NoSuchMethodException e1) {
				// TODO it is correct catch block with muffling
			}
			
			Method javaMethod = Class.forName(javaClassName).getMethod(javaMethodName, IMethodEnvironmentData.class);
			Object inputDoc = doc!=null?doc:getEnvData().getDisplayObjectModel().getObject();
			if (constructor!=null && inputDoc instanceof ODocument){
				Object newInstance = constructor.newInstance(inputDoc);
				javaMethod.invoke(newInstance,getEnvData());
			}else{
				javaMethod.invoke(null,getEnvData());
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String getTitleKey() {
		return annotation.titleKey();
	}
	
	protected String getJavaMethodName() {
		return javaMethodName;
	}

	protected String getJavaClassName() {
		return javaClassName;
	}

	protected ClassOMethod getAnnotation() {
		return annotation;
	}
	
}
