package org.orienteer.core.method.configs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * {@link OMethod} wrapper for annotations on java methods 
 *
 */
public class JavaMethodOMethodConfig extends AbstractOMethodConfig{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(JavaMethodOMethodConfig.class);
	
	private String javaMethodName;
	private Class<?> javaClass;

	public JavaMethodOMethodConfig(Method javaMethod){
		super(javaMethod.getDeclaringClass().getSimpleName()+"."+javaMethod.getName(), 
													javaMethod.getAnnotation(OMethod.class));
		this.javaMethodName = javaMethod.getName();
		this.javaClass = javaMethod.getDeclaringClass();
	}
	
	@Override
	public void invokeLinkedFunction(IMethodContext dataObject,ODocument doc) {
		try {
			Constructor<?> constructor=null;
			try {
				constructor = javaClass.getConstructor(ODocument.class);
			} catch (NoSuchMethodException e1) {
				// TODO it is correct catch block with muffling
			}
			
			Method javaMethod = javaClass.getMethod(javaMethodName, IMethodContext.class);
			Object inputDoc = doc!=null?doc:dataObject.getDisplayObjectModel().getObject();
			if (constructor!=null && inputDoc instanceof ODocument){
				Object newInstance = constructor.newInstance(inputDoc);
				javaMethod.invoke(newInstance,dataObject);
			}else{
				javaMethod.invoke(null,dataObject);
			}
		} catch (IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException 
				| SecurityException | InstantiationException e) {
			LOG.error("Error during method invokation", e);
		} 
	}

	public String getJavaMethodName() {
		return javaMethodName;
	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

}
