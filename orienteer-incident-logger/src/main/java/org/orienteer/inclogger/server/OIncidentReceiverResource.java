package org.orienteer.inclogger.server;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.io.IOUtils;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.inclogger.core.interfaces.IReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Data provider for {@link OIncidentReceiver}. 
 * 
 */
public class OIncidentReceiverResource extends AbstractResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MOUNT_PATH = "/rest/incident";
	public static final String REGISTRATION_RES_KEY=OIncidentReceiverResource.class.getSimpleName();
	
	private static final Logger LOG = LoggerFactory.getLogger(OIncidentReceiverResource.class);
	
	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		final WebRequest request = (WebRequest) attributes.getRequest();
		final HttpServletRequest httpRequest = (HttpServletRequest) request.getContainerRequest();
		final ResourceResponse response = new ResourceResponse();
		response.setContentType("text/plain");
		if(response.dataNeedsToBeWritten(attributes))
		{
			String out="OK";
			try
			{
				if(httpRequest.getMethod().equalsIgnoreCase("GET") //for debug 
						|| httpRequest.getMethod().equalsIgnoreCase("POST") )
				{
					StringWriter received = new StringWriter();
					IOUtils.copy(httpRequest.getInputStream(), received);

					LOG.info("received="+received);
					getReceiver().receive(received.toString());
				}
			} catch (Throwable e)
			{
				LOG.error("Error", e);
				String message = e.getMessage();
				if(message==null) message = "Error";
				out = message;
			}
			final String finalOut = out;

			response.setWriteCallback(new WriteCallback() {
				@Override
				public void writeData(Attributes attributes) throws IOException {
					attributes.getResponse().write(finalOut);
				}
			});
		}
		return response;
	}
	
	private IReceiver getReceiver(){
		return OIncidentReceiver.INSTANCE;
	}
	
	public static void mount(WebApplication app)
	{
		OIncidentReceiverResource resource = ((OrienteerWebApplication) app).getServiceInstance(OIncidentReceiverResource.class);
		app.getSharedResources().add(REGISTRATION_RES_KEY, resource);
		app.mountResource(MOUNT_PATH, new SharedResourceReference(REGISTRATION_RES_KEY));
	}
}

