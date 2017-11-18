package org.orienteer.core.method.configs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.behavior.Behavior;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * {@link ClassOMethod} annotation wrapper
 *
 */
public class OClassOMethodConfig extends AbstractOMethodConfig{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(OClassOMethodConfig.class);
	
	private ClassOMethod oMethod;
	private transient List<IMethodFilter> filters;
	private List<Class<? extends Behavior>> behaviors;
	private String javaMethodName;
	private Class<?> javaClass;

	public OClassOMethodConfig(ClassOMethod oMethod,Method javaMethod){
		this.oMethod = oMethod;
		behaviors = Arrays.asList(oMethod.behaviors());
		this.javaMethodName = javaMethod.getName();
		this.javaClass = javaMethod.getDeclaringClass();
	}
	
	@Override
	public String titleKey() {
		return oMethod.titleKey();
	}
	@Override
	public FAIconType icon() {
		return oMethod.icon();
	}
	@Override
	public BootstrapType bootstrap() {
		return oMethod.bootstrap();
	}
	@Override
	public boolean changingDisplayMode() {
		return oMethod.changingDisplayMode();
	}
	@Override
	public boolean changingModel() {
		return oMethod.changingModel();
	}
	@Override
	public int order() {
		return oMethod.order();
	}
	@Override
	public List<IMethodFilter> filters() {
		if (filters==null){
			filters = makeFilters(oMethod.filters()); 
		}
		return filters;
	}
	@Override
	public List<Class<? extends Behavior>> behaviors() {
		return behaviors;
	}
	
	public boolean resetSelection(){
		return oMethod.resetSelection();
	}

	@Override
	public void invokeLinkedFunction(IMethodEnvironmentData dataObject,ODocument doc) {
		try {
			Constructor<?> constructor=null;
			try {
				constructor = javaClass.getConstructor(ODocument.class);
			} catch (NoSuchMethodException e1) {
				// TODO it is correct catch block with muffling
			}
			
			Method javaMethod = javaClass.getMethod(javaMethodName, IMethodEnvironmentData.class);
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
