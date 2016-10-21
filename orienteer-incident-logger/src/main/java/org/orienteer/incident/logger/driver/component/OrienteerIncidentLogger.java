package org.orienteer.incident.logger.driver.component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.service.impl.OrienteerWebjarsSettings;

import ru.asm.utils.incident.logger.core.AbstractLogger;
import ru.asm.utils.incident.logger.core.ILoggerData;

/**
 * 
 */
public class OrienteerIncidentLogger extends AbstractLogger{

	static SimpleDateFormat ft =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssXXX");//w3c datetime format
	
	public OrienteerIncidentLogger(ILoggerData<?> data) {
		super(data);
	}
	
	protected void writeData(Throwable e){
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    PrintStream printStream = new PrintStream(stream);
	    e.printStackTrace(printStream);
	    printStream.flush();
	    data.set("StackTrace", stream.toString());
	    writeData(e.getMessage());
	}
	
	protected void writeData(String message){
	    Package objPackage = this.getClass().getPackage(); 
	    
	    String appname = objPackage.getSpecificationTitle();
	    String appver = objPackage.getSpecificationVersion();
	      
	    data.set("Application", appname+ " v"+appver);
	    data.set("DateTime", ft.format(new Date()));
	    data.set("UserName", System.getProperty("user.name"));
	    data.set("Message", message);
	    data.end();
	}
}
