import groovy.json.JsonBuilder

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.protocol.HTTP
import org.apache.http.util.EntityUtils

def jsonBuilder = new JsonBuilder()
jsonBuilder {
	firstname  'Иван'
	lastname  'Иванов'
	email 'ivan.ivanov@gmail.com'
	telephone '555-1234'
	contacttype (
		id : 1
	)
}

String content = jsonBuilder.toString()

println content

HttpEntity requestBody = new StringEntity(content, 'UTF-8')

DefaultHttpClient httpclient = new DefaultHttpClient();
try {
	httpclient.credentialsProvider.setCredentials(
			new AuthScope("localhost", 8080),
			new UsernamePasswordCredentials("user1", "1111"));

	HttpPost httppost = new HttpPost("http://localhost:8080/contactmanager-mockmvc/ws/add");
	
	httppost.entity = requestBody
	
	println "executing request $httppost.requestLine" 
	
	HttpResponse response = httpclient.execute(httppost);
	HttpEntity entity = response.getEntity();

	System.out.println("----------------------------------------");
	System.out.println(response.getStatusLine());
	if (entity != null) {
		System.out.println("Response content length: " + entity.getContentLength());
		System.out.println("Response content length: " + entity.getContent().getText());
	}
	EntityUtils.consume(entity);
} finally {
	// When HttpClient instance is no longer needed,
	// shut down the connection manager to ensure
	// immediate deallocation of all system resources
	httpclient.getConnectionManager().shutdown();
}
