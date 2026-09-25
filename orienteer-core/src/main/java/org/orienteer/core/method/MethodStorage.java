package org.orienteer.core.method;

import java.util.HashSet;
import java.util.Set;

import org.orienteer.core.boot.loader.OrienteerClassLoader;
import org.orienteer.core.component.command.Command;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;

/**
 * 
 * Storage for {@link IMethod} classes
 *
 */
public class MethodStorage {
	
	private static final String CORE_PATH = "org.orienteer.core";

	private Set<Class<?>> methodClasses;
	private Set<String> paths;

	private Set<java.lang.reflect.Method> methodFields;
	
	public MethodStorage() {
		paths = new HashSet<String>();
		paths.add(CORE_PATH);
		reload();
	}
	
	public void reload(){
		Set<java.lang.reflect.Method> newMethodFields = new HashSet<>();
		Set<Class<?>> newMethodClasses = new HashSet<>();
		String annotation = OMethod.class.getName();
		try(ScanResult scan = new ClassGraph()
									.overrideClassLoaders(OrienteerClassLoader.getClassLoader())
									.acceptPackages(paths.toArray(new String[0]))
									.enableClassInfo()
									.enableMethodInfo()
									.enableAnnotationInfo()
									.ignoreClassVisibility()
									.ignoreMethodVisibility()
									.scan()) {
			for(ClassInfo classInfo : scan.getClassesWithMethodAnnotation(annotation)) {
				for(MethodInfo methodInfo : classInfo.getDeclaredMethodInfo()) {
					if(methodInfo.hasAnnotation(annotation)) newMethodFields.add(methodInfo.loadClassAndGetMethod());
				}
			}
			for(ClassInfo classInfo : scan.getClassesWithAnnotation(annotation)) {
				newMethodClasses.add(classInfo.loadClass());
			}
		}
		newMethodClasses.removeIf(c -> !IMethod.class.isAssignableFrom(c) && !Command.class.isAssignableFrom(c));
		methodFields = newMethodFields;
		methodClasses = newMethodClasses;
	}
	
	public void addPath(String path) {
		paths.add(path);
	}
	
	public void removePath(String path) {
		paths.remove(path);
	}
	
	public Set<Class<?>> getMethodClasses() {
		return methodClasses;
	}

	public Set<java.lang.reflect.Method> getMethodFields() {
		return methodFields;
	}

}
