package org.orienteer.logger.server;

import org.apache.wicket.authorization.AuthorizationException;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.core.request.mapper.StalePageException;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.string.Strings;
import org.orienteer.logger.IOLoggerConfiguration;
import org.orienteer.logger.IOLoggerEventDispatcher;
import org.orienteer.logger.OLoggerEvent;
import org.orienteer.logger.impl.DefaultOLoggerEventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IOLoggerEventDispatcher} for handle {@link OLoggerEvent}s within Orienteer: log localy and send to other host 
 */
public class EmbeddedOLoggerEventDispatcher extends DefaultOLoggerEventDispatcher {
	
	private static final Logger LOG = LoggerFactory.getLogger(EmbeddedOLoggerEventDispatcher.class);
	
	@Override
	public void dispatch(OLoggerEvent event) {
		if(needsToBeLogged(event)) {
			OLoggerModule.storeOLoggerEvent(event.toJson());
			super.dispatch(event);
		}
	}
	
	protected boolean needsToBeLogged(OLoggerEvent event) {
		Object seed = event.getSeed();
		if(seed instanceof Throwable) return needsToBeLogged((Throwable)seed);
		else return true;
	}
	
	protected boolean needsToBeLogged(Throwable event) {
		if(event instanceof StalePageException
				|| event instanceof AuthorizationException
				|| event instanceof UnauthorizedActionException) return false;
		else return true;
	}
	
	@Override
	public void configure(IOLoggerConfiguration configuration) {
		super.configure(configuration);
		if(!Strings.isEmpty(collectorUrl)) {
			Url url = Url.parse(collectorUrl);
			if(Strings.isEmpty(url.getPath())) {
				collectorUrl = collectorUrl+(collectorUrl.endsWith("/")?"":"/")+"rest/ologger";
			}
		}
	}
	
	@Override
	protected void syslog(OLoggerEvent event) {
		// NOP: it should be already logged
	}

	@Override
	protected void syslog(String message, Exception exc) {
		if(message==null && exc == null) return;
		else if(message!=null && exc == null) LOG.error(message);
		else if(message==null && exc != null) LOG.error("Error in dispatcher", exc);
		else LOG.error(message, exc);
	}
}
