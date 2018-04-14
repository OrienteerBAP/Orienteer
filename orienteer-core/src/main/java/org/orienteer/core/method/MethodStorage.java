package org.orienteer.core.method;

import java.util.HashSet;
import java.util.Set;

import org.orienteer.core.boot.loader.OrienteerClassLoader;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeElementsScanner;

/**
 * 
 * Storage for {@link IMethod} classes
 *
 */
public class MethodStorage {
	
	private static final String CORE_PATH = "org.orienteer.core";

	private Set<Class<? extends IMethod>> methodClasses;
	private Set<String> paths;

	private Set<java.lang.reflect.Method> methodFields;
	
	public MethodStorage() {
		paths = new HashSet<String>();
		paths.add(CORE_PATH);
		reload();
	}
	
	public void reload(){
		Reflections reflections = new Reflections(paths,
												  OrienteerClassLoader.getClassLoader(),
												  new TypeElementsScanner(),
												  new MethodAnnotationsScanner(),
												  new SubTypesScanner());
		methodFields = reflections.getMethodsAnnotatedWith(OMethod.class);
		methodClasses = reflections.getSubTypesOf(IMethod.class);
		methodClasses.removeIf(c -> !c.isAnnotationPresent(OMethod.class));
	}
	
	public void addPath(String path) {
		paths.add(path);
	}
	
	public void removePath(String path) {
		paths.remove(path);
	}
	
	public Set<Class<? extends IMethod>> getMethodClasses() {
		return methodClasses;
	}

	public Set<java.lang.reflect.Method> getMethodFields() {
		return methodFields;
	}

}
