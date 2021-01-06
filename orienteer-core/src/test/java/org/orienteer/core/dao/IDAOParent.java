package org.orienteer.core.dao;

public interface IDAOParent {
	
	public int methodWithNoBodyInParent();
	
	public default Class<?> methodWithDefaultBodyInParent() {
		return IDAOParent.class;
	}
	
	public default void methodVoidWithException() {
		throw new IllegalStateException("This shouldn't be thrown because overrided in child"); 
	}
	
	public void methodVoid();
}
