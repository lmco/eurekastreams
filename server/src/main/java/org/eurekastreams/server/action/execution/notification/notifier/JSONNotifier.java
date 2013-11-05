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

import java.io.StringWriter;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Notifier for REST endpoints, takes a template and sends it to a REST endpoint.
 *
 */
public class JSONNotifier implements Notifier
{
	/** Apache Velocity templating engine. */
    private final VelocityEngine velocityEngine;

    /** Global context for Apache Velocity templating engine. (Holds system-wide properties.) */
    private final Context velocityGlobalContext;

    /** Message templates by notification type. */    
    private final Map<NotificationType, String> templates;
    
    /** Templates detailing where to send the rest notifications. */
    private final Map<String, String> endpointTemplates;
    
    /** Lookup the recipient of the notification. */
    private final DomainMapper<Long, Person> placeholderPersonMapper;
    
    /** Logs. */
    private final Log logger = LogFactory.getLog(JSONNotifier.class);

    /**
     * Constructor.
     * @param inVelocityEngine
     * 			  Velocity engine used for templating
     * @param inVelocityGlobalContext
     *            Global context for Apache Velocity templating engine.
     * @param inTemplates
     *            Message templates by notification type.
     * @param inEndpointTemplates
     * 			  The templates containing the endpoints where the REST calls will be made.
     * @param inPlaceholderPersonMapper
     * 			  The mapper to lookup users
     */
    public JSONNotifier(final VelocityEngine inVelocityEngine, 
    		final Context inVelocityGlobalContext,
            final Map<NotificationType, String> inTemplates,
            final Map<String, String> inEndpointTemplates,
            final DomainMapper<Long, Person> inPlaceholderPersonMapper)
    {
        velocityEngine = inVelocityEngine;
        velocityGlobalContext = inVelocityGlobalContext;
        templates = inTemplates;
        endpointTemplates = inEndpointTemplates;
        placeholderPersonMapper = inPlaceholderPersonMapper;
    }

	@Override
	public Collection<UserActionRequest> notify(final NotificationType inType,
			final Collection<Long> inRecipients, 
			final Map<String, Object> inProperties,
			final Map<Long, PersonModelView> inRecipientIndex) throws Exception 
	{	
		Context velocityContext = new VelocityContext(new VelocityContext(inProperties, velocityGlobalContext));
        velocityContext.put("context", velocityContext);
        velocityContext.put("type", inType);
		
		for (long recipientId : inRecipients)
        {
            Person recipient = placeholderPersonMapper.execute(recipientId);
            if (recipient == null)
            {
                continue;
            }

        	String template = templates.get(inType);
            if (template == null)
            {
                return null;
            }

            velocityContext.put("recipient", recipient);
            StringWriter writer = new StringWriter();
            velocityEngine.evaluate(velocityContext, writer, "JsonNotification-" + inType, template);

            String message = writer.toString();
            sendJSONNotification(message, velocityContext);
  
        }
		
		return null;
		
	}

	/**
	 * Make the http call to the specified endpoint.
	 * @param message
	 * 			message to be sent to the endpoint
	 * @param velocityContext
	 * 			velocity engine used to format the message and endpoint
	 */
	private void sendJSONNotification(final String message, final Context velocityContext)
	{
		try
        {
			for (String key : endpointTemplates.keySet())
			{
    			StringWriter endpointString = new StringWriter();
    			velocityEngine.evaluate(velocityContext, 
    									endpointString, 
    									"jsonEnpoint", 
    									URLDecoder.decode(endpointTemplates.get(key), "UTF-8"));
    			
	            // Make the http request here.
	            URI endpoint = new URI(endpointString.toString());
	            logger.debug("Target url for json notifier request " + endpoint.toString());
	            
	            try
	            {
		            HttpClient client = new DefaultHttpClient();
		            HttpPost post = new HttpPost(endpoint);
		            post.setHeader("Content-Type", "application/json");
		            post.setEntity(new StringEntity(message));
		            client.execute(post);
	            }
	            catch (Exception ex)
	            {
	            	logger.debug("Error connecting to json notification url.", ex);
	            }
	        }
        }
        catch (Exception ex)
        {
            String msg = "Error occurred connection to json notifier endpoint " + ex;
            logger.error(msg, ex);
        }
	}
}
