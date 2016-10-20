package org.orienteer.incident.logger.driver.component;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;

import ru.asm.utils.incident.logger.core.ISender;

public class OrienteerIncidentSender implements ISender {

	public OrienteerIncidentSender() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean send(String input) {
		// Instantiate HttpClient
		HttpClient httpClient = new HttpClient();

		// Configure HttpClient, for example:
		//httpClient.setFollowRedirects(false);

		// Start HttpClient
		try {
			httpClient.start();
			ContentResponse response = httpClient.POST("http://localhost:8080/rest/incident")
		        .param("value", input)
		        .send();
			httpClient.stop();
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		/*
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://targethost/login");
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("username", "vip"));
		nvps.add(new BasicNameValuePair("password", "secret"));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response2 = httpclient.execute(httpPost);

		try {
		    System.out.println(response2.getStatusLine());
		    HttpEntity entity2 = response2.getEntity();
		    // do something useful with the response body
		    // and ensure it is fully consumed
		    EntityUtils.consume(entity2);
		} finally {
		    response2.close();
		}
		*/
		// TODO Auto-generated method stub
	//	return false;
	}

}
