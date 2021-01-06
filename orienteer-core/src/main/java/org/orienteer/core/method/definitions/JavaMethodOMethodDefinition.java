package org.orienteer.core.method.definitions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.wicket.request.RequestHandlerExecutor.ReplaceHandlerException;
import org.apache.wicket.util.lang.Exceptions;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.DAOOClass;
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
 * {@link OMethod} definition for annotations on java methods.
 * Class name should be equal to class name in DB or selector should be provided on every method 
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
			Method javaMethod = javaClass.getMethod(javaMethodName, IMethodContext.class);
			ODocument inputDoc = doc!=null?doc:(ODocument)dataObject.getDisplayObjectModel().getObject();
			
			Object instance = null;
			if(javaClass.isInterface()) {
				if(javaClass.isAnnotationPresent(DAOOClass.class))instance = DAO.provide(javaClass, inputDoc);
			} else {
				try {
					instance = javaClass.getConstructor(ODocument.class).newInstance(inputDoc);
				} catch (NoSuchMethodException e1) {
					// TODO it is correct catch block with muffling
				}
			}
			javaMethod.invoke(instance, dataObject);
		} catch (IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException 
				| SecurityException | InstantiationException e) {
			ReplaceHandlerException replaceHandlerException = Exceptions.findCause(e, ReplaceHandlerException.class);
			if(replaceHandlerException!=null) throw replaceHandlerException;
			else LOG.error("Error during method invokation", e);
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
		if(getSelector().isEmpty()) {
			DAOOClass daoOClass = javaClass.getAnnotation(DAOOClass.class);
			String selector = daoOClass!=null?daoOClass.value():javaClass.getSimpleName();
			ret.add(new SelectorFilter().setFilterData(selector));
		}
		return ret;
	}

}
