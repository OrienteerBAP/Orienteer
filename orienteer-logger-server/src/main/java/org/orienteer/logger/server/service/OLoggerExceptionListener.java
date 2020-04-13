package org.orienteer.logger.server.service;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.logger.OLogger;

/**
 * Wicket {@link IRequestCycleListener} for handle exceptions to {@link OLogger}}
 *
 */
public class OLoggerExceptionListener implements IRequestCycleListener{

	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception ex) {
		OLogger.log(ex);
		return null;
	}
	

}
