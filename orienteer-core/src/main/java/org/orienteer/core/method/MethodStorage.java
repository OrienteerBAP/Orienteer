package org.orienteer.core.method;

import java.util.HashSet;
import java.util.Set;

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
		reload();
	}
	
	public void reload(){
		Reflections reflections = new Reflections(CORE_PATH,new TypeElementsScanner(),new MethodAnnotationsScanner(),new SubTypesScanner());
		for (String path : paths) {
			reflections.merge(new Reflections(path,new TypeElementsScanner(),new MethodAnnotationsScanner(),new SubTypesScanner()));
		}
		try {
			methodFields = reflections.getMethodsAnnotatedWith(ClassMethod.class);
			methodClasses = reflections.getSubTypesOf(IMethod.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
