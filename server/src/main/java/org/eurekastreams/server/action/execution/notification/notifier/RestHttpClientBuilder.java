/*
 * Copyright (c) 2013 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eurekastreams.server.action.execution.notification.notifier;

import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.jboss.security.Base64Encoder;


/**
 * Implementation of class that posts to rest endpoints.
 *
 */
public class RestHttpClientBuilder implements HttpClientBuilder
{
	/** http client used to communicate with endpoint. */
	private final HttpClient httpClient;
	/** http post used to post data to httpClient. */
	private final HttpPost httpPost;
	/** username for user credentials using basic authentication. */
	private final String basicAuthUsername;
	/** password for user creditials using basic authentication. */
	private final String basicAuthPassword;
	/** logger. */
	private final Log logger = LogFactory.getLog(RestHttpClientBuilder.class);
	
	/** default constuctor. 
	 * 
	 * @param inClient
	 * 		The httpclient object to be used.
	 * @param inPost
	 * 		The http post object to be used.
	 * @param inBasicAuthUsername
	 * 		The username used for basic auth to endpoint.
	 * @param inBasicAuthPassword
	 * 		The password used for basic auth to endpoint
	 * 
	 * */
	public RestHttpClientBuilder(final HttpClient inClient,
            final HttpPost inPost,
            final String inBasicAuthUsername,
            final String inBasicAuthPassword)
	{
		httpClient = inClient;
		httpPost = inPost;
		basicAuthUsername = inBasicAuthUsername;
		basicAuthPassword = inBasicAuthPassword;
	}

	/**
	 * method which posts to the supplied endpoint.
	 * @param endpoint
	 * 		endpoint to post to.
	 * @param body
	 * 		message to post to endpoint
	 */
	@Override
	public void post(final URI endpoint, final String body) 
	{	
		String strResponse = "";
	    try
	    {
	    	if (!basicAuthUsername.isEmpty())
	    	{
	    		String encoding = "Basic " + Base64Encoder.encode(basicAuthUsername + ":" + basicAuthPassword);
	    		httpPost.setHeader("Authorization", encoding);
	    	}
	    	httpPost.setURI(endpoint);
	        httpPost.setHeader("Content-Type", "application/json");
	        httpPost.setEntity(new StringEntity(body));
	        HttpResponse response = httpClient.execute(httpPost);
	        strResponse = EntityUtils.toString(response.getEntity());
	    }
	    catch (Exception ex)
	    {
	    	logger.debug("Error connecting to json notification url. Response:"+strResponse, ex);
	    }
	}
}
