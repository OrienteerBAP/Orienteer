package org.orienteer.core.method.methods;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.IClassMethod;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodEnvironmentData;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * 
 * @author Asm
 *
 */

public abstract class AbstractOClassOMethod implements Serializable,IMethod,IClassMethod{

	private static final long serialVersionUID = 1L;
	protected IMethodEnvironmentData envData;
	protected String id;
	protected String javaMethodName;
	protected String javaClassName;
	protected ClassOMethod annotation;

	@Override
	public void initOClassMethod(Method javaMethod) {
		this.javaMethodName = javaMethod.getName();
		this.javaClassName = javaMethod.getDeclaringClass().getName();
		this.annotation = javaMethod.getAnnotation(ClassOMethod.class);
		
	}

	@Override
	public void methodInit(String id,IMethodEnvironmentData envData) {
		this.envData = envData;
		this.id = id;
	}
	
	protected SimpleNamingModel<String> getTitleModel(){
		if (!annotation.titleKey().isEmpty()){
			return new SimpleNamingModel<String>(annotation.titleKey());			
		}
		return new SimpleNamingModel<String>(id);
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
			Object inputDoc = doc!=null?doc:envData.getDisplayObjectModel().getObject();
			if (constructor!=null && inputDoc instanceof ODocument){
				Object newInstance = constructor.newInstance(inputDoc);
				javaMethod.invoke(newInstance,envData);
			}else{
				javaMethod.invoke(null,envData);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
