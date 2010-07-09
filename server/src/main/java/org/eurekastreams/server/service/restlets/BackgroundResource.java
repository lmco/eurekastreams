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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.persistence.BackgroundMapper;

/**
 * Used to supply autocomplete for various types of Background data.
 */
public class BackgroundResource extends SmpResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(BackgroundResource.class);
    
    /**
     * Mapper used to look up background data.
     */
    private BackgroundMapper backgroundMapper;

    /**
     * The BackgroundType of data being requested.
     */
    private BackgroundItemType backgroundType;

    /**
     * The characters to search with.
     */
    private String targetString;
    
    /**
     * Default max number of results to return.
     */
    private static final int DEFAULT_MAX_RESULTS = 10;
    
    /**
     * The key used in the JSON string. 
     */
    public static final String ITEM_NAMES_KEY = "itemNames"; 
    
    /**
     * Setter.
     * 
     * @param inBackgroundMapper
     *            the new mapper.
     */
    public void setBackgroundMapper(final BackgroundMapper inBackgroundMapper)
    {
        backgroundMapper = inBackgroundMapper;        
    }

    /**
     * Initialize parameters from the request object.
     *            the context of the request
     * @param request
     *            the client's request
     */
    protected void initParams(final Request request)
    {
        Map<String, Object> attributes = request.getAttributes();
        String type = (String) attributes.get("type");
        backgroundType = BackgroundItemType.valueOf(type.toUpperCase());

        //Attempt to decode using W3C standard encoding, if failure, 
        //try to retrieve string and pass through without decoding.
        try
        {
            targetString = URLDecoder.decode((String) attributes.get("query"), "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            targetString = (String) attributes.get("query");
            log.error("Unsupported encoding on input for: " + targetString);
        }
    }

    /**
     * Getter.
     * 
     * @return the backgroundType
     */
    public BackgroundItemType getbackgroundType()
    {
        return backgroundType;
    }

    /**
     * Setter.
     * 
     * @param inBackgroundType
     *            the backgroundType to set
     */
    public void setBackgroundType(final BackgroundItemType inBackgroundType)
    {
        backgroundType = inBackgroundType;
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
        
        //ensure backgroundType and targetString are non-null and useful.
        if (backgroundType == null || (backgroundType == BackgroundItemType.NOT_SET) 
                || targetString == null || targetString.length() == 0)
        {
            log.info("BackgroundType or TargetString arguements were null or not set."
                    + " No search attempted.");
            //TODO find out if this is this correct to send back as "null".
            return new StringRepresentation("");
        }
        
        List<String> results = backgroundMapper.findBackgroundItemNamesByType(
                backgroundType, targetString, DEFAULT_MAX_RESULTS);
        
        if (log.isDebugEnabled())
        {
            log.debug("Search with targetString: " + targetString + " and backgroundType: " 
                    + backgroundType + " returned " + results.size() + " results.");
        }
        
        JSONObject json = new JSONObject();
        JSONArray jsonResults = new JSONArray();
        
        for (String result : results)
        {                
            jsonResults.add(result);
        }
        json.put(ITEM_NAMES_KEY, jsonResults);
        
        if (log.isDebugEnabled())
        {
            log.debug("Search with targetString: " + targetString + " and backgroundType: " 
                    + backgroundType + " generated JSON: " + json.toString());
        }

        Representation rep = new StringRepresentation(json.toString(), MediaType.APPLICATION_JSON);
        rep.setExpirationDate(new Date(0L));
        return rep;
    }
}
