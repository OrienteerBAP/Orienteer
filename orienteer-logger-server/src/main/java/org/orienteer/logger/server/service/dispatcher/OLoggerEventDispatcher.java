package org.orienteer.logger.server.service.dispatcher;

import org.apache.wicket.authorization.AuthorizationException;
import org.apache.wicket.core.request.mapper.StalePageException;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.string.Strings;
import org.orienteer.logger.IOLoggerConfiguration;
import org.orienteer.logger.IOLoggerEventDispatcher;
import org.orienteer.logger.OLoggerEvent;
import org.orienteer.logger.impl.DefaultOLoggerEventDispatcher;
import org.orienteer.logger.server.model.IOLoggerDAO;
import org.orienteer.logger.server.model.IOLoggerEventModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.orientechnologies.orient.core.exception.OSecurityException;

/**
 * {@link IOLoggerEventDispatcher} for handle {@link OLoggerEvent}s within Orienteer: log localy and send to other host 
 */
public class OLoggerEventDispatcher extends DefaultOLoggerEventDispatcher {
	
	private static final Logger LOG = LoggerFactory.getLogger(OLoggerEventDispatcher.class);
	
	@Override
	public void dispatch(OLoggerEvent event) {
		if (needsToBeLogged(event)) {
			IOLoggerEventModel eventModel = IOLoggerDAO.INSTANCE.storeOLoggerEvent(event.toJson());
			onDispatchEvent(eventModel, event);
			super.dispatch(event);
		}
	}

	protected void onDispatchEvent(IOLoggerEventModel eventModel, OLoggerEvent event) {

	}

	protected boolean needsToBeLogged(OLoggerEvent event) {
		Object seed = event.getSeed();
		if (seed instanceof Throwable) {
			return needsToBeLogged((Throwable)seed);
		}
		return true;
	}
	
	protected boolean needsToBeLogged(Throwable event) {
		Throwable rootCause = Throwables.getRootCause(event);
		if (rootCause instanceof StalePageException 
				|| rootCause instanceof AuthorizationException
				|| rootCause instanceof OSecurityException) {
			return false;
		}
		return true;
	}
	
	@Override
	public void configure(IOLoggerConfiguration configuration) {
		super.configure(configuration);
		if(!Strings.isEmpty(collectorUrl)) {
			Url url = Url.parse(collectorUrl);
			if(Strings.isEmpty(url.getPath())) {
				collectorUrl = collectorUrl+(collectorUrl.endsWith("/")?"":"/")+"resource/ologger";
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
