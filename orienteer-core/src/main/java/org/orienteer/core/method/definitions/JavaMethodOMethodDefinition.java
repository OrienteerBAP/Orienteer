package org.orienteer.core.method.definitions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.MethodPlace;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.SelectorFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * {@link OMethod} wrapper for annotations on java methods 
 *
 */
public class JavaMethodOMethodDefinition extends AbstractOMethodDefinition{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(JavaMethodOMethodDefinition.class);
	
	private String javaMethodName;
	private Class<?> javaClass;
	
	public static boolean isSupportedClass(Class<? extends IMethod> methodClass){
		return methodClass.isAnnotationPresent(OMethod.class);
	}

	public JavaMethodOMethodDefinition(Method javaMethod){
		super(javaMethod.getDeclaringClass().getSimpleName()+"."+javaMethod.getName(), 
													javaMethod.getAnnotation(OMethod.class));
		this.javaMethodName = javaMethod.getName();
		this.javaClass = javaMethod.getDeclaringClass();
	}
	
	@Override
	public IMethod getMethod(IMethodContext dataObject) {
		try {
			IMethod newMethod=null;
			if(MethodPlace.DATA_TABLE.equals(dataObject.getPlace())){
				newMethod = getTableIMethodClass().newInstance();
			}else{
				newMethod = getIMethodClass().newInstance();
			}
			if (newMethod!=null){
				newMethod.init(this, dataObject);
				return newMethod;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Can't obtain a method", e);
		}
		return null;
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
	
	@Override
	protected List<IMethodFilter> makeFilters(OFilter[] filters) {
		List<IMethodFilter> ret = super.makeFilters(filters);
		if(getSelector().isEmpty()) 
			ret.add(new SelectorFilter().setFilterData(javaClass.getSimpleName()));
		return ret;
	}

}
