/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.eurekastreams.server.domain.CompositeEntity;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.CompositeEntityMapper;

/**
 * REST endpoint for an entity overview.
 */
public class CompositeEntityOverviewResource extends WritableResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(CompositeEntityOverviewResource.class);

    /**
     * Mapper to retrieve an entity.
     */
    private CompositeEntityMapper entityMapper;

    /**
     * The short name of the entity whose description is being requested.
     */
    private String entityShortName;

    /**
     * The key used in the JSON string.
     */
    public static final String OVERVIEW_KEY = "overview";

    /**
     * The key used to identify the coordinators array.
     */
    public static final String COORDINATORS_KEY = "coordinators";

    /**
     * The key used to identify the coordinator's account id.
     */
    public static final String ACCOUNTID_KEY = "accountId";

    /**
     * Initialize parameters from the request object.
     *            the context of the request
     * @param request
     *            the client's request
     */
    protected void initParams(final Request request)
    {
        entityShortName = (String) request.getAttributes().get("shortName");
    }

    /**
     * This method is only here to help in testing. I want to find a better way to get spring involved in the setup, and
     * to have spring inject a mocked mapper.
     * 
     * @param inEntityMapper
     *            mapper.
     */
    public void setEntityMapper(final CompositeEntityMapper inEntityMapper)
    {
        entityMapper = inEntityMapper;
    }

    /**
     * Handles GET request.
     * 
     * @param variant
     *            the variant whose representation must be returned
     * @return representation of the entity description
     * @throws ResourceException
     *             on error
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        CompositeEntity entity = entityMapper.findByShortName(entityShortName);

        Set<Person> coordinators = entity.getCoordinators();

        JSONObject json = new JSONObject();

        // add the overview
        json.put(OVERVIEW_KEY, entity.getOverview());

        JSONArray jsonCoordinators = new JSONArray();
        for (Person person : coordinators)
        {
            jsonCoordinators.add(person.getOpenSocialId());
        }
        json.put(COORDINATORS_KEY, jsonCoordinators);

        StringRepresentation response = new StringRepresentation(json.toString(), MediaType.APPLICATION_JSON);
        
        response.setExpirationDate(new Date(0L));
        
        log.debug("Sending response: " + response.getText());

        return response;
    }

    /**
     * Handle PUT requests.
     * 
     * @param entity
     *            the resource's new representation
     * @throws ResourceException
     *             hopefully not
     */
    @Override
    public void storeRepresentation(final Representation entity) throws ResourceException
    {
        CompositeEntity cmpEntity = entityMapper.findByShortName(entityShortName);

        try
        {
            String text = entity.getText();
            log.debug("PUT entity overview for " + entityShortName + ": " + text);

            JSONObject jsonObject = JSONObject.fromObject(text);
            String overview = jsonObject.getString(OVERVIEW_KEY);
            cmpEntity.setOverview(overview);
            entityMapper.flush();
        }
        catch (IOException e)
        {
            log.error("Unable to read update to overview for " + entityShortName, e);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error with the provided Representation");
        }
        catch (Exception ex)
        {
            log.error("Unable to store the update for overview for " + entityShortName, ex);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error storing the provided Representation");
        }
    }
}
