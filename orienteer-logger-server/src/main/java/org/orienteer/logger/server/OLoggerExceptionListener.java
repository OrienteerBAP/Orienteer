package org.orienteer.logger.server;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.lang.Exceptions;
import org.orienteer.logger.OLogger;

import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.exception.OSecurityException;
import com.orientechnologies.orient.core.exception.OValidationException;

/**
 * Wicket {@link IRequestCycleListener} for handle exceptions to {@link OLogger}}
 *
 */
public class OLoggerExceptionListener extends AbstractRequestCycleListener{

	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception ex) {
		OLogger.log(ex);
		return null;
	}
	

}
