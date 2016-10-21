package org.orienteer.incident.logger.driver.component;

import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.B64Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.asm.utils.incident.logger.core.ISender;

/**
 * Http sender. Used Basic HTTP auth. 
 *
 */
public class OrienteerIncidentSender implements ISender {

	private static final Logger LOG = LoggerFactory.getLogger(OrienteerIncidentSender.class);

	String login;
	String password;
	String receiverUrl;
	
	public OrienteerIncidentSender(String login, String password,String receiverUrl) {
		this.login=login;
		this.password=password;
		this.receiverUrl=receiverUrl;
	}

	@Override
	public boolean send(String input) {
		try {
			HttpClient httpClient = new HttpClient();
			httpClient.start();
			Request request = httpClient.POST(receiverUrl);
	        request.header(HttpHeader.ACCEPT, "text/plain");
	        request.header(HttpHeader.CONTENT_TYPE, "text/plain");
	        request.header("Authorization", "Basic " + B64Code.encode(login + ":" + password, StandardCharsets.ISO_8859_1));
	        request.content(new StringContentProvider(input), "text/plain");
	        ContentResponse response = request.send();
	        
			httpClient.stop();
			if (response.getContentAsString().equals("OK")){
				return true;
			}  
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
