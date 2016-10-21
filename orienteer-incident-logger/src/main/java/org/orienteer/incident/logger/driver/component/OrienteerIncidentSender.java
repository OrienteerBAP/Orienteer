package org.orienteer.incident.logger.driver.component;

import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.B64Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.asm.utils.incident.logger.core.ISender;

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
			ContentResponse response = httpClient.POST(receiverUrl).
					method("POST").
					header("Authorization", "Basic " + B64Code.encode(login + ":" + password, StandardCharsets.ISO_8859_1))
		        .param("value", input)
		        .send();
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
