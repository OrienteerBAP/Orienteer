package org.orienteer.incident.logger.driver.component;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.request.resource.AbstractResource.ResourceResponse;
import org.apache.wicket.request.resource.AbstractResource.WriteCallback;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.orienteer.core.OrienteerWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.asm.utils.incident.logger.IncidentLogger;
import ru.asm.utils.incident.logger.core.ILogger;
import ru.asm.utils.incident.logger.core.IReciever;

public class testresource extends AbstractResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MOUNT_PATH = "/rest/incident/test";
	public static final String REGISTRATION_RES_KEY=testresource.class.getSimpleName();
	
	
	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		final WebRequest request = (WebRequest) attributes.getRequest();
		//final boolean checking = attributes.getParameters().get("check").toBoolean(false);
		final HttpServletRequest httpRequest = (HttpServletRequest) request.getContainerRequest();
		final ResourceResponse response = new ResourceResponse();
		response.setContentType("text/plain");
		//response.setContentType("application/json");
		if(response.dataNeedsToBeWritten(attributes))
		{
			String out="lalala";
			final String finalOut = out;
	        ILogger logger = IncidentLogger.get().makeLogger();
	        logger.incident("testresource incident!!!!11111");
	        
			response.setWriteCallback(new WriteCallback() {
				@Override
				public void writeData(Attributes attributes) throws IOException {
					attributes.getResponse().write(finalOut);
				}
			});
		}
		return response;
	}
	
	public static void mount(WebApplication app)
	{
		testresource resource = ((OrienteerWebApplication) app).getServiceInstance(testresource.class);
		app.getSharedResources().add(REGISTRATION_RES_KEY, resource);
		app.mountResource(MOUNT_PATH, new SharedResourceReference(REGISTRATION_RES_KEY));
	}
}


