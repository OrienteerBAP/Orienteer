package org.orienteer.core.tasks;

/**
 * Original process callback for make it manageable 
 *
 */
@FunctionalInterface
public interface ITaskSessionCallback {
	public void interrupt() throws Exception;
}
