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
	    data.set("stackTrace", stream.toString());
	    writeData(e.getMessage());
	}
	
	protected void writeData(String message){
	    Package objPackage = this.getClass().getPackage(); 
	    
	    String appname = objPackage.getSpecificationTitle();
	    String appver = objPackage.getSpecificationVersion();
	      
	    data.set("application", appname+ " v"+appver);
	    data.set("dateTime", ft.format(new Date()));
	    data.set("userName", System.getProperty("user.name"));
	    data.set("message", message);
	    data.end();
	}
}
