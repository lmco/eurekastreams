/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.restlets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.service.ActionController;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * This class handles the REST endpoint for retrieving non locked users from the Eureka Streams db includes data for
 * ntid, email and all configured ldap properties.
 * 
 */
public class PersonPropertiesResourceSingle extends SmpResource
{
    /**
     * Local logger instance for this class.
     */
    private final Log logger = LogFactory.make();

    /**
     * Service action controller for execution the actions for this restlet.
     */
    private final ActionController serviceActionController;

    /**
     * id of the user for which you want the properties.
     */
    private String uuid = null;
    
    /**
     * Action for retrieving all of the Person objects in the db only populating accountid and additionalproperties.
     */
    private final ServiceAction getAllPersonAdditionalPropertiesActionSingle;

    /**
     * Constructor.
     * 
     * @param inServiceActionController
     *            - instance of the ServiceActionController for executing actions.
     * @param inGetAllPersonAdditionalPropertiesActionSingle
     *            - instance of the action used to retreive all of the db ids for the users in the db.
     */
    public PersonPropertiesResourceSingle(final ActionController inServiceActionController,
            final ServiceAction inGetAllPersonAdditionalPropertiesActionSingle)
    {
        serviceActionController = inServiceActionController;
        getAllPersonAdditionalPropertiesActionSingle = inGetAllPersonAdditionalPropertiesActionSingle;
    }

    /**
     * {@inheritDoc}. Nothing needed here because there are no params to initialize.
     */
    @Override
    protected void initParams(final Request request)
    {
        uuid = (String) request.getAttributes().get("uuid");
    }
    /**
     * {@inheritDoc}. Retrieve the information from the actions and return back a json string that contains the
     * accountid, and additional properties map for each user in the db.
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        String jsString = new String();

        JSONObject responseJson = new JSONObject();
        
        List<Map<String, Object>> people;
        
        try
        {
            people = (ArrayList<Map<String, Object>>) serviceActionController.execute(
            		new ServiceActionContext(uuid, null), getAllPersonAdditionalPropertiesActionSingle);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving the users from the db.", ex);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }
		
        JSONArray personProperties = new JSONArray();
        JSONObject personJs;
        
        for (Map<String, Object> currentPersonProperties : people)
        {
            personJs = new JSONObject();
            personJs.put("accountId", (String) currentPersonProperties.get("accountId"));
            personJs.put("email", (String) currentPersonProperties.get("email"));
            if (currentPersonProperties.get("additionalProperties") != null)
            {
                for (Entry<String, String> currentAddlProperty : ((Map<String, String>) currentPersonProperties
                        .get("additionalProperties")).entrySet())
                {
                    personJs.put(currentAddlProperty.getKey(), currentAddlProperty.getValue());
                }
            }
            personProperties.add(personJs);
        }
        responseJson.put("personPropertiesSingle", personProperties);
        
        Representation rep = new StringRepresentation(responseJson.toString(), MediaType.APPLICATION_JSON);
        rep.setExpirationDate(new Date(0L));
        
        return rep;
    }

}
