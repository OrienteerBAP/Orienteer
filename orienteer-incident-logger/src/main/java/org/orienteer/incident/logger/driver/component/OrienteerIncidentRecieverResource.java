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

import ru.asm.utils.incident.logger.core.IReciever;
import ru.asm.utils.incident.logger.core.IServer;
import ru.asm.utils.incident.logger.core.Server;

public class OrienteerIncidentRecieverResource extends AbstractResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MOUNT_PATH = "/rest/incident";
	public static final String REGISTRATION_RES_KEY=OrienteerIncidentRecieverResource.class.getSimpleName();
	
	private static final Logger LOG = LoggerFactory.getLogger(OrienteerIncidentRecieverResource.class);
	
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
			try
			{
				if(httpRequest.getMethod().equalsIgnoreCase("GET"))
				{
					out="GET lalala";
					String recieved = attributes.getParameters().get("value").toOptionalString();
					if (recieved!=null){
						LOG.info("content="+recieved);
						getReciever().recieve(recieved);
					}
				}
				else
				{
					out="NONGET lalala";
					String recieved = attributes.getParameters().get("value").toOptionalString();
					LOG.info("content="+recieved);
					getReciever().recieve(recieved);
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
	
	private IReciever getReciever(){
		return OrienteerIncidentReciever.INSTANCE;
	}
	
	public static void mount(WebApplication app)
	{
		OrienteerIncidentRecieverResource resource = ((OrienteerWebApplication) app).getServiceInstance(OrienteerIncidentRecieverResource.class);
		app.getSharedResources().add(REGISTRATION_RES_KEY, resource);
		app.mountResource(MOUNT_PATH, new SharedResourceReference(REGISTRATION_RES_KEY));
	}
}

