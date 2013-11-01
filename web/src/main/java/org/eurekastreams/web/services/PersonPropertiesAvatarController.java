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
package org.eurekastreams.web.services;
 
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.ReadMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * This class handles the REST endpoint for retrieving non locked users from the Eureka Streams db. 
 * It takes in a json array in the format [{"id":"ntid", "avatarId":"avatarId"}], it will check if
 * the avatarId has changed and only return the base64 encoded image if it is different.
 */
public class PersonPropertiesAvatarController
{
    /**
     * Local logger instance for this class.
     */
    private final Log logger = LogFactory.make();

    /**
     * Service action controller for execution the actions for this restlet.
     */
    private String imagePrefix = "n";
    
    /**
     * mapper to read people from db.
     */
    private final DomainMapper<List<String>, List<PersonModelView>> peopleMapper;
    
    /**
     * mapper to read avatars from db.
     */
    private final ReadMapper<List<String>, List<Map<String, Object>>> avatarMapper;
    /** JSON Factory for building JSON Generators. */
    private final JsonFactory jsonFactory;
    /** JSON object mapper. */
    private final ObjectMapper jsonObjectMapper;
    
    /**
     * Constructor.
     * 
     * @param inGetPersonModelViewsByAccountIdsMapper
     *            - gets all users avatarId by ntid.
     * @param inGetAllPersonAvatarIdMapper
     *            - gets all users image blobs
     * @param inJsonFactory	
     * 			  - used to build the json response
     * @param inJsonObjectMapper
     *            - used to build the json response
     */
    public PersonPropertiesAvatarController(
    		final DomainMapper<List<String>, List<PersonModelView>> inGetPersonModelViewsByAccountIdsMapper,
            final ReadMapper<List<String>, List<Map<String, Object>>> inGetAllPersonAvatarIdMapper,
            final JsonFactory inJsonFactory,
            final ObjectMapper inJsonObjectMapper)
    {
        peopleMapper = inGetPersonModelViewsByAccountIdsMapper;
        avatarMapper = inGetAllPersonAvatarIdMapper;
        jsonFactory = inJsonFactory;
        jsonObjectMapper = inJsonObjectMapper;
    }

    /**
     * {@inheritDoc}. return the ntid, avatarid and imageBlob if it is neede
     */
    @RequestMapping(value = "json", method = RequestMethod.GET)
    public void getAvatars(@RequestParam("urlJson") final String urlJson, final HttpServletResponse response)
    {
    	List<PersonModelView> people = null;
    	Map<String, String> avatarIdToPeopleMap = new HashMap<String, String>();
        List<Map<String, Object>> avatars = null;
        String json;

        //parse the json input from the url
        try 
        {
			json = URLDecoder.decode(urlJson, "UTF-8");
		} 
        catch (UnsupportedEncodingException e) 
        {
			logger.error("Invalid incoming JSON");
			throw new ExecutionException("Invalid JSON.");
		}        
        JSONArray jsonArray = JSONArray.fromObject(json);
        
        //get users from url list and create map of users
        try
        {
        	List<String> peopleIdsToFetch = new ArrayList<String>();
            for (Object jsonItem:jsonArray)
            {
            	String accountId = ((JSONObject) jsonItem).get("id").toString();
            	String avatarId = ((JSONObject) jsonItem).get("avatarId").toString();
                peopleIdsToFetch.add(accountId);
                if(avatarId!=null && !avatarId.isEmpty())
                {
                	avatarIdToPeopleMap.put(avatarId, accountId);
                }
            }
            
            // fetch the people
            people = peopleMapper.execute(peopleIdsToFetch);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving the users from the db.", ex);
            throw new ExecutionException("error retrieving users");
        }
        
        //create new object to be passed back as json and create list of avatarIds to be passed to the db query
        JSONArray personProperties = new JSONArray();
        List<String> avatarIdList = new ArrayList<String>();
        for (PersonModelView currentPersonProperties : people)
        {
			if(!avatarIdToPeopleMap.containsKey(currentPersonProperties.getAvatarId()))
	    	{
	        	avatarIdList.add(imagePrefix+currentPersonProperties.getAvatarId());
	    	}
        }
        
        //get all avatar by avatarId
        try
        {
        	avatars = avatarMapper.execute(avatarIdList);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving the users from the db.", ex);
            throw new ExecutionException("Error retieving avatars");
        }
        
        //loop through and build json object for response
        for (PersonModelView currentPersonProperties : people)
        {
        	JSONObject p = new JSONObject();
			p.put("id", currentPersonProperties.getAccountId());
			p.put("avatarId", currentPersonProperties.getAvatarId());
        	//if it's not the same send back base64 image
    		if(avatarIdList.contains(imagePrefix+currentPersonProperties.getAvatarId()))
    		{
    			for(Map<String, Object>currentAvatar:avatars)
    			{
	        		String imageId = (String) currentAvatar.get("imageIdentifier");
	        		if((imagePrefix+currentPersonProperties.getAvatarId()).equals(imageId))
	        		{
		    			byte[] baseBytes = Base64.encodeBase64((byte[]) currentAvatar.get("imageBlob"));
		    			String baseString = new String(baseBytes);
		    			p.put("imageBlob", baseString);
	        		}
    			}
    		}
    		personProperties.add(p);
        }
        
        //return response
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.addHeader("Pragma", "no-cache");
        response.setHeader("Content-Type", "application/json");
        JsonGenerator jsonGenerator;
		try 
		{
			jsonGenerator = jsonFactory.createJsonGenerator(response.getWriter());
			jsonObjectMapper.writeValue(jsonGenerator, personProperties);
		} 
		catch (IOException e) 
		{
			logger.error("error creating json", e);
			throw new ExecutionException("error creating json format");
		}
    }
}
