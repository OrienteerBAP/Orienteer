package ru.ydn.orienteer.standalone;

import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerRunner
{
	public static final int DEFAULT_TIMEOUT = 60*60*1000;
	public static final int DEFAULT_PORT = 8080;
	
	private int timeout = DEFAULT_TIMEOUT;
	private int port = DEFAULT_PORT;
	
	private Server server;
	
	public ServerRunner()
	{
	}
	
	public ServerRunner(int port)
	{
		this.port = port;
	}
	
	public ServerRunner(int port, int timeout)
	{
		this.port = port;
		this.timeout = timeout;
	}



	public void start() throws Exception
	{
		if(server==null)
		{
	        server = new Server();
	        SocketConnector connector = new SocketConnector();
	
	        // Set some timeout options to make debugging easier.
	        connector.setMaxIdleTime(timeout);
	        connector.setSoLingerTime(-1);
	        connector.setPort(port);
	        server.addConnector(connector);
	        Resource.setDefaultUseCaches(false);
	
	        WebAppContext bb = new WebAppContext();
	        bb.setServer(server);
	        bb.setContextPath("/");
	        ProtectionDomain protectionDomain = StartStandalone.class.getProtectionDomain();
	        URL location = protectionDomain.getCodeSource().getLocation();
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
