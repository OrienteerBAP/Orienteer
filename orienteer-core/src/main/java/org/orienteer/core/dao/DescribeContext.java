package org.orienteer.core.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

import org.apache.wicket.util.collections.MultiMap;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DescribeContext {
	
	class ContextItem {
		private Class<?> daoClass;
		private String oClass;
		private List<Supplier<Boolean>> postponedTillExit = new ArrayList<Supplier<Boolean>>();
		private MultiMap<String, Supplier<Boolean>> postponedTillDefined = new MultiMap<String, Supplier<Boolean>>();
		
		ContextItem(Class<?> daoClass, String oClass) {
			this.daoClass = daoClass;
			this.oClass = oClass;
		}
	}
	
	private Map<Class<?>, String> describedClasses = new HashMap<Class<?>, String>();
	
	private Stack<Class<?>> processingStackIndex = new Stack<Class<?>>();
	private Stack<ContextItem> processingStack = new Stack<ContextItem>();
	
	private MultiMap<String, Supplier<Boolean>> globalPostponedTillDefined = new MultiMap<String, Supplier<Boolean>>();
	

	public void entering(Class<?> clazz, String oClass) {
		if(processingStackIndex.contains(clazz)) throw new IllegalStateException("Class "+clazz.getName()+" is already in stack. Stop infinite loop.");
		processingStackIndex.push(clazz);
		processingStack.push(new ContextItem(clazz, oClass));
	}
	
	public void exiting(Class<?> clazz, String oClassName) {
		Class<?> exiting = processingStackIndex.pop();
		if(!clazz.equals(exiting)) throw new IllegalStateException("Exiting from wrong execution: expected "+clazz.getName()+" but in a stack "+exiting.getName());
		ContextItem last = processingStack.pop();
		if(!oClassName.equals(last.oClass))  throw new IllegalStateException("Exiting from wrong execution: expected "+oClassName+" but in a stack "+last.oClass);
		for (Supplier<Boolean> postponed : last.postponedTillExit) {
			postponed.get();
		}
		describedClasses.put(clazz, oClassName);
		
		MultiMap<String, Supplier<Boolean>> mergeTo = processingStack.empty()?globalPostponedTillDefined:processingStack.lastElement().postponedTillDefined;
		Iterator<Map.Entry<String, List<Supplier<Boolean>>>> it = last.postponedTillDefined.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, List<Supplier<Boolean>>> entry = it.next();
			if(wasDescribed(entry.getKey())) {
				for (Supplier<Boolean> supplier : entry.getValue()) {
					supplier.get();
				}
				it.remove();
			} else {
				List<Supplier<Boolean>> mergeToValue = mergeTo.get(entry.getKey());
				if(mergeToValue!=null) mergeToValue.addAll(entry.getValue());
				else mergeTo.put(entry.getKey(), entry.getValue());
			}
		}
		
	}
	
	public boolean inStack(Class<?> clazz) {
		return processingStackIndex.contains(clazz);
	}
	
	public boolean wasDescribed(Class<?> clazz) {
		return describedClasses.containsKey(clazz);
	}
	
	public boolean wasDescribed(String oClass) {
		return describedClasses.containsValue(oClass);
	}
	
	public String getOClass(Class<?> clazz) {
		return describedClasses.get(clazz);
	}
	
	public String getOClassFromStack(Class<?> clazz) {
		int indx = processingStackIndex.indexOf(clazz);
		return indx>=0?processingStack.get(indx).oClass:null;
	}
	
	public String resolveOClass(Class<?> clazz, Supplier<String> supplier) {
		String ret = getOClass(clazz);
		if(Strings.isEmpty(ret)) ret = getOClassFromStack(clazz);
		return !Strings.isEmpty(ret) ? ret : supplier.get();
	}
	
	public void postponeTillExit(Supplier<Boolean> supplier) {
		processingStack.lastElement().postponedTillExit.add(supplier);
	}
	
	public void postponeTillDefined(String linkedClass, Supplier<Boolean> supplier) {
		if(wasDescribed(linkedClass)) postponeTillExit(supplier);
		else processingStack.lastElement().postponedTillDefined.addValue(linkedClass, supplier);
//		if(wasDescribed(linkedClass) || processingStack.lastElement().oClass.equals(linkedClass)) postponTillExit(supplier);
	}
	
	public void close(boolean restrictDependencies) {
		if(processingStackIndex.size()>0) throw new IllegalStateException("Can't close context because stack is not null");
		Collection<List<Supplier<Boolean>>> remaining = globalPostponedTillDefined.values();
		if(restrictDependencies && remaining.size()>0) throw new IllegalStateException("There are unsitisfied dependencies");
		for (List<Supplier<Boolean>> list : remaining) {
			for (Supplier<Boolean> supplier : list) {
				supplier.get();
			}
		}
	}

}
