package org.orienteer.standalone;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.orienteer.core.OrienteerWebApplication;

/**
 * Class with main method to start Orienteer in standalone mode
 */
public class StartStandalone
{
	private static final Pattern ARG_PATTERN=Pattern.compile("^--([^=]*)=?(.*)$");
	private static final String ARG_CONFIG="config";
	private static final String ARG_EMBEDDED="embedded";
	private static final String ARG_HOST="host";
	private static final String ARG_PORT="port";
	private static final String ARG_HELP="help";
	private static final String ARG_WAIT="wait";
	
	private StartStandalone() {
	}
	
    public static void main(String[] args) throws Exception {
    	
    	Map<String, String> parsedArgs = parseArgs(args);
    	if(parsedArgs==null || parsedArgs.containsKey(ARG_HELP))
    	{
    		printHelp();
    		return;
    	}
    	if(parsedArgs.containsKey(ARG_CONFIG))
    	{
    		System.setProperty(PROPERTIES_FILE_NAME, parsedArgs.get(ARG_CONFIG));
    	}
    	else if(parsedArgs.containsKey(ARG_EMBEDDED))
    	{
    		System.setProperty(PROPERTIES_FILE_NAME, StartStandalone.class.getResource("standalone.properties").toString());
    	}
    	else
    	{
    		URL url = lookupPropertiesURL();
    		if(url!=null)
    		{
    			System.out.println("Automatic lookup found following config file: "+url);
    			System.setProperty(PROPERTIES_FILE_NAME, url.toString());
    		}
    		else
    		{
    			System.out.println("Using embedded mode");
    			System.setProperty(PROPERTIES_FILE_NAME, StartStandalone.class.getResource("standalone.properties").toString());
    		}
    	}
    	try {
    		int port = ServerRunner.DEFAULT_PORT;
    		String portStr = parsedArgs.get(ARG_PORT);
    		try
			{
				if(portStr!=null) port = Integer.parseInt(portStr);
			} catch (NumberFormatException e)
			{
				System.out.println("Port '"+portStr+"' is incorrect. Using default port "+port);
			}
    		String host = parsedArgs.get(ARG_HOST);
    		ServerRunner runner = new ServerRunner(host, port);
            System.out.println("Starting Orienteer on "+(host!=null?host:"0.0.0.0")+":"+port);
            runner.start();
            String wait = parsedArgs.get(ARG_WAIT);
            if(wait==null || wait.trim().length()==0)
            {
            	System.out.println("PRESS ANY KEY TO STOP");
            	System.in.read();
            }
            else
            {
            	System.out.println("ENTER '"+wait+"' TO STOP");
            	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            	String line=reader.readLine();
            	while(!wait.equals(line))
            	{
            		if(line!=null) System.out.printf("Input is '%s'. You should enter '%s' to exit", line, wait);
            		else Thread.sleep(60000);
            	}
            }
            System.out.println("Stopping Orienteer");
            runner.stop();
            runner.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static Map<String, String> parseArgs(String... args)
    {
    	Map<String, String> ret = new HashMap<String, String>();
    	Matcher matcher;
    	for(String arg: args)
    	{
    		matcher = ARG_PATTERN.matcher(arg);
    		if(matcher.find())
    		{
    			ret.put(matcher.group(1), matcher.group(2));
    		}
    		else
    		{
    			System.out.printf("Unknown option provided '%s'", arg);
    			return null;
    		}
    	}
    	return ret;
    }
    
    private static void printHelp()
    {
    	ProtectionDomain protectionDomain = StartStandalone.class.getProtectionDomain();
    	URL location = protectionDomain.getCodeSource().getLocation();
    	System.out.printf("Usage: java -jar %s [--config=<path> | --embedded] [--host=<host>] [--port=<port>] [--wait=<wait for>][--help]", location.getFile());
    }
    
    //TODO: There is code duplication! Remove
    public static final String PROPERTIES_FILE_NAME = "orienteer.properties";
	
	public static URL lookupPropertiesURL() throws IOException
	{
		String configFile = System.getProperty(PROPERTIES_FILE_NAME);
		if(configFile!=null)
		{
			File file = new File(configFile);
			if(file.exists())
			{
				return file.toURI().toURL();
			}
			else
			{
				URL url = OrienteerWebApplication.class.getClassLoader().getResource(configFile);
				if(url!=null) return url;
				else return new URL(configFile);
			}
		}
		else
		{
			File file = new File(PROPERTIES_FILE_NAME);
			File dir = new File("").getAbsoluteFile();
			while(!file.exists() && dir!=null)
			{
				dir = dir.getParentFile();
				file = new File(dir, PROPERTIES_FILE_NAME);
			}
			return file!=null && file.exists() ?file.toURI().toURL():null;
		}
	}

	
}
