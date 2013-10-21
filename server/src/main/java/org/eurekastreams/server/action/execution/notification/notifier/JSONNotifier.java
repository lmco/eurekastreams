/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.implement.EscapeHtmlReference;
import org.apache.velocity.context.Context;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.email.NotificationEmailDTO;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.execution.notification.notifier.EmailNotificationTemplate.ReplyAction;
import org.eurekastreams.server.domain.HasEmail;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.ActivityInteractionType;
import org.eurekastreams.server.service.actions.strategies.links.ConnectionFacade;
import org.eurekastreams.server.service.email.TokenContentEmailAddressBuilder;
import org.eurekastreams.server.service.email.TokenContentFormatter;
import org.eurekastreams.server.service.utility.authorization.ActivityInteractionAuthorizationStrategy;

public class JSONNotifier implements Notifier
{
	/** Apache Velocity templating engine. */
    private final VelocityEngine velocityEngine;

    /** Global context for Apache Velocity templating engine. (Holds system-wide properties.) */
    private final Context velocityGlobalContext;

    /** Message templates by notification type. */    
    private final Map<NotificationType, String> templates;
    
    private final Map<String, String> endpointTemplates;
    
    private final DomainMapper<Long, Person> placeholderPersonMapper;
    
    private final Log logger = LogFactory.getLog(JSONNotifier.class);
    
    private final String endpoint = "http://166.17.46.89:8003/notifications/gapsokar";


    /**
     * Constructor.
     *
     * @param inVelocityEngine
     *            Apache Velocity templating engine.
     * @param inVelocityGlobalContext
     *            Global context for Apache Velocity templating engine.
     * @param inTemplates
     *            Message templates by notification type.
     * @param inSubjectPrefix
     *            Prefix to use on email subjects.
     * @param inTokenContentFormatter
     *            Builds the token content.
     * @param inTokenAddressBuilder
     *            Builds the recipient email address with a token.
     * @param inActivityAuthorizer
     *            For determining if users can comment on an activity.
     * @param inSendHtml
     *            If HTML emails will be sent. (These will be multipart with a plain text component.)
     */
    public JSONNotifier(final VelocityEngine inVelocityEngine, final Context inVelocityGlobalContext,
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
	public Collection<UserActionRequest> notify(NotificationType inType,
			Collection<Long> inRecipients, Map<String, Object> inProperties,
			Map<Long, PersonModelView> inRecipientIndex) throws Exception 
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
            SendJSONNotification(message, velocityContext);
  
        }
		
		return null;
		
	}
	
	private void SendJSONNotification(final String message, final Context velocityContext)
	{
		try
        {
			for(String key : endpointTemplates.keySet())
			{
    			StringWriter endpointString = new StringWriter();
    			velocityEngine.evaluate(velocityContext, endpointString, "jsonEnpoint", endpointTemplates.get(key));
    			
	            // Make the http request here.
	            URL endpoint = new URL(endpointString.toString());
	            logger.debug("Target url for json notifier request " + endpoint.toString());
	            HttpURLConnection urlConnection = null;
	            try
	            {
	            	
	                // Open the HttpConnection and make the POST.
	                urlConnection = (HttpURLConnection) endpoint.openConnection();
	                urlConnection.setRequestMethod("POST");
	                urlConnection.setDoOutput(true);
	                urlConnection.setRequestProperty("Content-Type", "application/json");
	
	                OutputStreamWriter out = null;
	                try
	                {
	                	out = new OutputStreamWriter(urlConnection.getOutputStream());
	                	out.write(message);
	                	out.flush();
	                	out.close();
	                }
	                catch (IOException e)
	                {
	                    String ioErrorMsg = "IOException occurred writing to lmlaunch outputstream.";
	                    logger.error(ioErrorMsg, e);
	                    throw new Exception(ioErrorMsg);
	                }
	                finally
	                {
	                    if (out != null)
	                    {
	                        out.close();
	                    }
	                }
	            }
	            catch (IOException ioex)
	            {
	                throw ioex;
	            }
	            finally
	            {
	                if (urlConnection != null)
	                {
	                    urlConnection.disconnect();
	                }
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
