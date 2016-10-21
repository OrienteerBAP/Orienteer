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
import ru.asm.utils.incident.logger.core.IReceiver;

/**
 * Page for testing {@link IncidentLoggerModule} 
 *
 */
public class Testresource extends AbstractResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MOUNT_PATH = "/rest/incident/test";
	public static final String REGISTRATION_RES_KEY=Testresource.class.getSimpleName();
	
	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		final ResourceResponse response = new ResourceResponse();
		response.setContentType("text/plain");
        String out="some out";

		//program init
        IncidentLogger.init(new OrienteerIncidentConfigurator());
		ILogger logger = IncidentLogger.get().makeLogger();
        try{
        	//body of program
        	logger.message("Initial data, or other stuff, send only if we call incident after this");
            if (true){
                throw new Exception("Unexpectedly exception");
            }
            IncidentLogger.close();
        	//body of program end
	    }catch (Exception e) {
	        logger.incident(e);
		}

		final String finalOut = out;
	        
		response.setWriteCallback(new WriteCallback() {
			@Override
			public void writeData(Attributes attributes) throws IOException {
				attributes.getResponse().write(finalOut);
			}
		});
		return response;
	}
	
	public static void mount(WebApplication app)
	{
		Testresource resource = ((OrienteerWebApplication) app).getServiceInstance(Testresource.class);
		app.getSharedResources().add(REGISTRATION_RES_KEY, resource);
		app.mountResource(MOUNT_PATH, new SharedResourceReference(REGISTRATION_RES_KEY));
	}
}


