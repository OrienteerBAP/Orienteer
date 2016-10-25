package org.orienteer.inclogger;

import java.io.IOException;


import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.inclogger.IncidentLogger;
import org.orienteer.inclogger.core.OIncidentConfigurator;
import org.orienteer.inclogger.core.interfaces.ILogger;

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
	protected ResourceResponse newResourceResponse(Attributes attributes){
		final ResourceResponse response = new ResourceResponse();
		response.setContentType("text/plain");
        String out="some out";

		//program init
        //IncidentLogger.init(new OIncidentConfigurator());
		//ILogger logger = IncidentLogger.get().makeLogger();
        //try{
        	//body of program
        	//logger.message("Initial data, or other stuff, send only if we call incident after this");
        	String t = null;
        	t.toString();
            //if (true){
            //    throw new IOException("Really unexpectedly exception!");
            //}
            //IncidentLogger.close();
        	//body of program end
	    //}catch (Exception e) {
	        //logger.incident(e);
		//}

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


