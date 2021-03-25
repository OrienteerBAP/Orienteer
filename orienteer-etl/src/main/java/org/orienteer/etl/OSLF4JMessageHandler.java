package org.orienteer.etl;

import com.orientechnologies.orient.output.OPluginMessageHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom implementation of {@link OPluginMessageHandler}
 * Needed till https://github.com/orientechnologies/orientdb/issues/9559 resolution
 */
@Slf4j
public class OSLF4JMessageHandler implements OPluginMessageHandler {

	private static final OSLF4JMessageHandler INSTANCE = new OSLF4JMessageHandler();
	
	private OSLF4JMessageHandler() {
		
	}
	
	public static OSLF4JMessageHandler getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void warn(Object requester, String format, Object... args) {
		if(args!=null && args[0] instanceof Throwable) log.warn(String.format(format, args), args[0]);
		else log.warn(format, args);
	}
	
	@Override
	public void warn(Object requester, String message) {
		log.warn(message);
	}
	
	@Override
	public void setOutputManagerLevel(int outputManagerLevel) {
		
	}
	
	@Override
	public void info(Object requester, String format, Object... args) {
		if(args!=null && args[0] instanceof Throwable) log.info(String.format(format, args), args[0]);
		else log.info(format, args);
	}
	
	@Override
	public void info(Object requester, String message) {
		log.info(message);
	}
	
	@Override
	public int getOutputManagerLevel() {
		return 0;
	}
	
	@Override
	public void error(Object requester, String format, Object... args) {
		if(args!=null && args[0] instanceof Throwable) log.error(String.format(format, args), args[0]);
		else log.error(format, args);
	}
	
	@Override
	public void error(Object requester, String message) {
		log.error(message);
	}
	
	@Override
	public void debug(Object requester, String format, Object... args) {
		if(args!=null && args[0] instanceof Throwable) log.debug(String.format(format, args), args[0]);
		else log.debug(format, args);
	}
	
	@Override
	public void debug(Object requester, String message) {
		log.debug(message);
	}
}
