/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.util.URIComponentUtils;
import org.eurekastreams.server.persistence.mappers.SearchPeopleAndGroupsByPrefix;
import org.eurekastreams.server.persistence.mappers.requests.GetEntitiesByPrefixRequest;
import org.eurekastreams.server.search.modelview.DisplayEntityModelView;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Restlet for returning person/group entity model views by prefix.
 */
public class EntitiesByPrefixResource extends WritableResource
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.getLog(BackgroundResource.class);

    /**
     * Mapper for getting followed entities.
     */
    private SearchPeopleAndGroupsByPrefix entitiesDAO;

    /**
     * The key used in the JSON string.
     */
    protected static final String ENTITIES_KEY = "entities";

    /**
     * The key used in the JSON string.
     */
    protected static final String NAME_KEY = "displayName";

    /**
     * The key used in the JSON string.
     */
    protected static final String TYPE_KEY = "entityType";

    /**
     * The key used in the JSON string.
     */
    protected static final String UNIQUEID_KEY = "uniqueId";

    /**
     * StreamScopeId for returned entity.
     */
    protected static final String STREAMSCOPEID_KEY = "streamScopeId";

    /**
     * The characters to search with.
     */
    private String targetString;

    /**
     * The key used in the JSON string.
     */
    public static final String ITEM_NAMES_KEY = "itemNames";

    /**
     * Setter.
     * 
     * @param inEntitiesDAO
     *            The mapper.
     */
    public void setEntitiesDAO(final SearchPeopleAndGroupsByPrefix inEntitiesDAO)
    {
        entitiesDAO = inEntitiesDAO;
    }

    /**
     * Initialize parameters from the request object. the context of the request
     * 
     * @param request
     *            the client's request
     */
    @Override
    protected void initParams(final Request request)
    {
        Map<String, Object> attributes = request.getAttributes();
        targetString = URIComponentUtils.decodeURIComponent((String) attributes.get("query"));
    }

    /**
     * Handle GET requests.
     * 
     * @param variant
     *            the variant to be retrieved.
     * @throws ResourceException
     *             thrown if a representation cannot be provided
     * @return a representation of the resource
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        try
        {
            // ensure targetString is non-null and useful.
            if (targetString == null || targetString.length() == 0)
            {
                log.info("TargetString arguement null or not set." + " No search attempted.");
                // TODO: find out if this is correct to send back as "null".
                return new StringRepresentation("");
            }

            // get current user accountId
            String acctId = SecurityContextHolder.getContext().getAuthentication().getName();

            List<DisplayEntityModelView> results = entitiesDAO.execute(new GetEntitiesByPrefixRequest(acctId.trim()
                    .toLowerCase(), targetString));

            JSONObject json = new JSONObject();

            JSONArray jsonEntities = new JSONArray();
            for (DisplayEntityModelView femv : results)
            {
                // Do not include Locked Accounts as part of autocomplete data
                if (!femv.isAccountLocked())
                {
                    jsonEntities.add(convertDisplayEntityModelViewToJSON(femv));
                }
            }
            json.put(ENTITIES_KEY, jsonEntities);
            log.debug("EntitiesByPrefixResource: json =   " + json.toString());

            Representation rep = new StringRepresentation(json.toString(), MediaType.APPLICATION_JSON);
            rep.setExpirationDate(new Date(0L));
            return rep;
        }
        catch (Exception ex)
        {
            log.error("Error occurred retrieving entity.", ex);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error occurred retrieving entity.", ex);
        }
    }

    /**
     * Method to convert {@link DisplayEntityModelView} to a JSON object.
     * 
     * @param femv
     *            {@link DisplayEntityModelView}.
     * @return JSON object representing the {@link DisplayEntityModelView}.
     */
    private Object convertDisplayEntityModelViewToJSON(final DisplayEntityModelView femv)
    {
        JSONObject jsonEntityObject = new JSONObject();
        jsonEntityObject.put(NAME_KEY, femv.getDisplayName().replace("'", "\'"));
        jsonEntityObject.put(TYPE_KEY, femv.getType().toString());
        jsonEntityObject.put(UNIQUEID_KEY, femv.getUniqueKey());
        jsonEntityObject.put(STREAMSCOPEID_KEY, femv.getStreamScopeId());

        return jsonEntityObject;
    }
}
