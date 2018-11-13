package org.orienteer.core.method.definitions;

import java.lang.reflect.Constructor;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.methods.CommandWrapperMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * 
 * {@link OMethod} definition for annotations on java classes.
 * Only classes inherited from {@link IMethod} or {@link OClass} are supported 
 *
 */
public class JavaClassOMethodDefinition extends AbstractOMethodDefinition{
	
	private static final Logger LOG = LoggerFactory.getLogger(JavaClassOMethodDefinition.class);

	private Class<?> javaClass;
	
	public static boolean isSupportedClass(Class<?> methodClass){
		return IMethod.class.isAssignableFrom(methodClass) 
				|| Command.class.isAssignableFrom(methodClass);
	}
	
	public JavaClassOMethodDefinition(Class<?> methodClass) {
		super(methodClass.getSimpleName(), methodClass.getAnnotation(OMethod.class));
		this.javaClass = methodClass;
	}
	
	@Override
	public IMethod getMethod(IMethodContext context) {
		try {
			IMethod newMethod = null;
			if(IMethod.class.isAssignableFrom(javaClass)) {
				newMethod = (IMethod) javaClass.newInstance();
			} else if(Command.class.isAssignableFrom(javaClass)) {
				newMethod = new CommandWrapperMethod() {
					
					@Override
					public Command<?> getWrappedCommand(String id) {
						try {
							Constructor<?> c = javaClass.getConstructor(String.class, IModel.class);
							return (Command<?>) c.newInstance(id, getContext().getDisplayObjectModel());
						} catch (Exception e) {
							LOG.error("Can't create a command", e);
							return null;
						} 
					}
				};
			}
			if(newMethod!=null) {
				newMethod.init(this,context);
				return newMethod;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Can't obtain a method", e);
		}
		return null;
	}
	
}
