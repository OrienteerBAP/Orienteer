package org.orienteer.standalone;

import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Utility class to run jetty server according to specified parameters
 */
public class ServerRunner
{
	public static final int DEFAULT_TIMEOUT = 60*60*1000;
	public static final int DEFAULT_PORT = 8080;
	
	private String host = null;
	private int port = DEFAULT_PORT;
	private int timeout = DEFAULT_TIMEOUT;
	
	private Server server;
	
	public ServerRunner()
	{
	}
	
	public ServerRunner(String host, int port)
	{
		this.host = host;
		this.port = port;
	}
	
	public ServerRunner(String host, int port, int timeout)
	{
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}



	public void start() throws Exception
	{
		if(server==null)
		{
	        server = new Server();
	        
	        HttpConfiguration httpConfig = new HttpConfiguration();
			httpConfig.setSecureScheme("https");
			httpConfig.setSecurePort(8443);
			httpConfig.setOutputBufferSize(32768);

			ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
			if(host!=null) http.setHost(host);
			http.setPort(8080);
			http.setIdleTimeout(timeout);

			server.addConnector(http);
			
	        Resource.setDefaultUseCaches(false);
	
	        WebAppContext bb = new WebAppContext();
	        bb.setServer(server);
	        bb.setContextPath("/");
	        ProtectionDomain protectionDomain = ServerRunner.class.getProtectionDomain();
	        URL location = protectionDomain.getCodeSource().getLocation();
	        System.out.println("loading from "+location);
	        bb.setWar(location.toExternalForm());
	        bb.setExtractWAR(false);
	        bb.setCopyWebInf(true);
	        bb.setCopyWebDir(false);
	
	        // START JMX SERVER
	        // MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
	        // MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
	        // server.getContainer().addEventListener(mBeanContainer);
	        // mBeanContainer.start();
	
	        server.setHandler(bb);
            server.start();
		}
	}
	
	public void stop() throws Exception
	{
		if(server!=null)server.stop();
	}
	
	public void join() throws Exception
	{
		if(server!=null) server.join();
	}
}
