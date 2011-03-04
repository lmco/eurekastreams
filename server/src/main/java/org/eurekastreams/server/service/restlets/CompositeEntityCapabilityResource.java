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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.CompositeEntity;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.CompositeEntityMapper;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
/**
 * Restlet for read/write entity capabilities.
 *
 */
public class CompositeEntityCapabilityResource extends WritableResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(CompositeEntityCapabilityResource.class);
    
    /**
     * The composite entity mapper.
     */
    private CompositeEntityMapper entityMapper;
    
    /**
     * The short name of the entity whose capabilities are being requested.
     */
    private String entityShortName;
    
    /**
     * The key used in the JSON string. 
     */
    public static final String CAP_ARRAY_KEY = "capabilities";
    
    /**
     * The key used in the JSON string. 
     */
    public static final String COORD_ARRAY_KEY = "coordinators";
    
    /**
     * The key used for entity short name from url parameters.
     */
    public static final String URL_SHORTNAME_KEY = "shortName";
    
    /**
     * Pattern for input validation. Allows zero or more alphanumeric , spaces, and .,-#!@$%^&*()'
     */
    private static final String REGEXP_PATTERN = 
            "[(a-zA-Z0-9\\s\\.,\\-\\#\\(\\)\\!\\@\\$\\%\\^\\&\\*\\')]*";

    /**
     * Initialize parameters from the request object. the context of the request
     * 
     * @param request
     *            the client's request
     */
    @Override
    protected void initParams(final Request request)
    {
        entityShortName = (String) request.getAttributes().get(URL_SHORTNAME_KEY);
    }

    /**
     * Set the composite entity mapper.
     * 
     * @param inEntMapper
     *            The composite entity mapper.
     */
    public void setEntityMapper(final CompositeEntityMapper inEntMapper)
    {
        entityMapper = inEntMapper;
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
        verifyEntityShortNameParam();                
                           
        CompositeEntity ent = this.getEntityByShortName(entityShortName);
        Representation rep = new StringRepresentation(convertEntityToJSONObject(ent).toString(), 
                MediaType.APPLICATION_JSON);
        
        rep.setExpirationDate(new Date(0L));
        
        return rep;
    }
    
    /**
     * Handle PUT requests for persisting representations.
     * 
     * @param entity
     *            The representation to persist.
     * @throws ResourceException
     *             If error occurs.
     */
    @Override
    public void storeRepresentation(final Representation entity) throws ResourceException
    {
        verifyEntityShortNameParam();
        
        //get entity first because if this fails, no reason to do anything else.
        CompositeEntity cmpEnt = (CompositeEntity) this.getEntityByShortName(entityShortName);
        
        try
        {            
            String jsontext = entity.getText();
            JSONObject jsonObject = JSONObject.fromObject(jsontext);
            JSONArray jsonCapabilities = jsonObject.getJSONArray(CAP_ARRAY_KEY);
            
            //create list of capabilities from json.
            List<BackgroundItem> capabilities = new ArrayList<BackgroundItem>(jsonCapabilities.size());
            for (Object capability : jsonCapabilities)
            {
               if (Pattern.matches(REGEXP_PATTERN, (String) capability))
               {
                   capabilities.add(new BackgroundItem((String) capability, 
                           BackgroundItemType.CAPABILITY));
               }
               else
               {
                   String msg = "Capability: " + (String) capability + " contains invalid characters.  "
                       + "Valid characters include alphanumeric, space, (!@$%^&*#-.,')";
                   log.error(msg);
                   JSONObject validationErrors = new JSONObject();
                   validationErrors.put(VALIDATION_ERRORS_KEY, msg);
                   getAdaptedResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                   getAdaptedResponse().setEntity(validationErrors.toString(), MediaType.APPLICATION_JSON);
                   return;
               }
            }
                        
            cmpEnt.setCapabilities(capabilities);
            entityMapper.flush();
            
        }
        catch (Exception e)
        {
            String msg = "Unable to persist entity capabilities to overview for "
                + entityShortName;
            log.error(msg, e);

            getAdaptedResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            getAdaptedResponse().setEntity(msg, MediaType.TEXT_PLAIN);
        }
  
    }
    
    /**
     * Converts entity object to JSONobject.
     * @param inEntInfo The object to convert.
     * @return JSONObject JSON representation of entity object.
     */
    public JSONObject convertEntityToJSONObject(final CompositeEntity inEntInfo)
    {
        JSONObject json = new JSONObject();
        JSONArray jsonCapArray = new JSONArray();
        JSONArray jsonCoordArray = new JSONArray();
        
        //Retrieve and add capabilities
        List<BackgroundItem> capabilities = inEntInfo.getCapabilities();
        
        if (log.isDebugEnabled())
        {
            log.debug("Searched capabilities with entity short name: " + entityShortName 
                    + " and found " + capabilities.size() + " results.");
        }
                
        for (BackgroundItem b : capabilities)
        {
            jsonCapArray.add(b.getName());
        }        
        json.put(CAP_ARRAY_KEY, jsonCapArray);
        
        //Retrieve and add coordinators
        Set<Person> coordinators = inEntInfo.getCoordinators();
        for (Person p : coordinators)
        {
            jsonCoordArray.add(p.getOpenSocialId());
        }
        json.put(COORD_ARRAY_KEY, jsonCoordArray);
        
        if (log.isDebugEnabled())
        {
            log.debug("Entity capability search with short name: " + entityShortName 
                    + " generated JSON: " + json.toString());
        }
        
        return json;
    }
    
    /**
     * Verifies that entity short name from request is not null or empty.
     * @throws ResourceException If short name is null or empty.
     */
    private void verifyEntityShortNameParam() throws ResourceException
    {
      //ensure entShortName is non-null and useful.
        if (entityShortName == null || entityShortName.trim().isEmpty())
        {
            String msg = "Invalid entity short name: " 
                + (entityShortName == null ? "null" : entityShortName)
                + " No search attempted.";
            log.info(msg);            
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, msg);
        }        
    }  
    
    /**
     * Return entity by short name or throws exception if not found.
     * @param inEntShortName Short name of entity to find.
     * @return The entity.
     * @throws ResourceException If entity not found.
     */
    private CompositeEntity getEntityByShortName(final String inEntShortName) throws ResourceException
    {
        CompositeEntity cmpEnt = entityMapper.findByShortName(entityShortName);
        if (cmpEnt == null)
        {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                    "Unable to find enitity with short name: " + entityShortName + ".");  
        }
        return cmpEnt;
    }
    

}
