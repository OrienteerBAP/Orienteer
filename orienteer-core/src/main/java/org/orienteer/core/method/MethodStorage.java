package org.orienteer.core.method;

import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

/**
 * 
 * Storage for {@link IMethod} classes
 *
 */
public class MethodStorage {
	
	private static final String CORE_PATH = "org.orienteer.core";

	Set<Class<? extends IMethod>> methodClasses;
	Set<String> paths;
	
	public MethodStorage() {
		paths = new HashSet<String>();
		reload();
	}
	
	public void reload(){
		Reflections reflections = new Reflections(CORE_PATH);
		for (String path : paths) {
			reflections.merge(new Reflections(path));
		}
		methodClasses = reflections.getSubTypesOf(IMethod.class);
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

}
