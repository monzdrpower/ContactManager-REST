import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils

DefaultHttpClient httpclient = new DefaultHttpClient();

try {
	httpclient.credentialsProvider.setCredentials(
			new AuthScope("localhost", 8080),
			new UsernamePasswordCredentials("admin", "pass"))

	HttpGet httppost = new HttpGet("http://localhost:8080/contactmanager-mockmvc/ws/delete/1")
	
	println "executing request $httppost.requestLine" 
	
	HttpResponse response = httpclient.execute(httppost)
	HttpEntity entity = response.entity

	println "----------------------------------------"
	println response.statusLine
	
	if (entity) {
		println "Response content length: ${entity.contentLength}"
		println "Response content length: ${entity.content.text}" 
	}
	EntityUtils.consume(entity)
} finally {
	// When HttpClient instance is no longer needed,
	// shut down the connection manager to ensure
	// immediate deallocation of all system resources
	httpclient.connectionManager.shutdown()
}
