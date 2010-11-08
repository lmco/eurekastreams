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
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * This class handles the REST endpoint for retrieving all non locked users from the Eureka Streams db includes data for
 * ntid, and all configured ldap properties.
 * 
 */
public class PersonPropertiesResource extends SmpResource
{
    /**
     * Local logger instance for this class.
     */
    private final Log logger = LogFactory.make();

    /**
     * Action used to retrieve all of the PersonModelViews for the ids passed in.
     */
    private final ServiceAction getPersonModelViewsByIdsAction;

    /**
     * Service action controller for execution the actions for this restlet.
     */
    private final ServiceActionController serviceActionController;

    /**
     * Action for retrieving all of the db ids for the users in the database.
     */
    private final ServiceAction getAllPersonIdsAction;

    /**
     * Constructor.
     * 
     * @param inGetPersonModelViewsByIdsAction
     *            - instance of the action used to retrieve all of the PersonModelView objects based on the db ids
     *            passed in.
     * @param inServiceActionController
     *            - instance of the ServiceActionController for executing actions.
     * @param inGetAllPersonIdsAction
     *            - instance of the action used to retreive all of the db ids for the users in the db.
     */
    public PersonPropertiesResource(final ServiceAction inGetPersonModelViewsByIdsAction,
            final ServiceActionController inServiceActionController, final ServiceAction inGetAllPersonIdsAction)
    {
        getPersonModelViewsByIdsAction = inGetPersonModelViewsByIdsAction;
        serviceActionController = inServiceActionController;
        getAllPersonIdsAction = inGetAllPersonIdsAction;
    }

    /**
     * {@inheritDoc}. Nothing needed here because there are no params to initialize.
     */
    @Override
    protected void initParams(final Request request)
    {
        // There are no parameters to init since this service is deterministic.
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

        List<PersonModelView> people;

        try
        {
            ArrayList<String> accountIds = (ArrayList<String>) serviceActionController.execute(
                    new ServiceActionContext(null, null), getAllPersonIdsAction);
            people = (List<PersonModelView>) serviceActionController.execute(
                    new ServiceActionContext(accountIds, null), getPersonModelViewsByIdsAction);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving the users from the db.", ex);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }

        JSONArray personProperties = new JSONArray();
        JSONObject personJs;
        for (PersonModelView currentPerson : people)
        {
            personJs = new JSONObject();
            personJs.put("accountId", currentPerson.getAccountId());
            if (currentPerson.getAdditionalProperties() != null)
            {
                for (Entry<String, String> currentAddlProperty : currentPerson.getAdditionalProperties().entrySet())
                {
                    personJs.put(currentAddlProperty.getKey(), currentAddlProperty.getValue());
                }
            }
            personProperties.add(personJs);
        }
        responseJson.put("personProperties", personProperties);

        Representation rep = new StringRepresentation(responseJson.toString(), MediaType.APPLICATION_JSON);
        rep.setExpirationDate(new Date(0L));

        return rep;
    }

}
