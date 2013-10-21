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
package org.eurekastreams.server.service.restlets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
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
 * This class handles the REST endpoint for retrieving non locked users from the Eureka Streams db 
 * it takes in a json array in the format [{"id":"ntid","avatarId":"avatarId"}], it will check if
 * the avatarId has changed and only return the base64 encoded image if it is different
 */
public class PersonPropertiesResourceAvatar extends SmpResource
{
    /**
     * Local logger instance for this class.
     */
    private final Log logger = LogFactory.make();

    /**
     * Service action controller for execution the actions for this restlet.
     */
    private final ActionController serviceActionController;

    private String urlJson = null;
    private String IMAGE_PREFIX = "n";
    
    /**
     * Action for retrieving all of the Person objects in the db only populating accountid and additionalproperties.
     */
    private final ServiceAction getAllPersonAvatar;
    private final ServiceAction getAllPersonAvatarId;

    /**
     * Constructor.
     * 
     * @param inServiceActionController
     *            - instance of the ServiceActionController for executing actions.
     * @param inGetAllPersonAvatar
     *            - gets all users avatarId by ntid.
     * @param inGetAllPersonAvatarId
     *            - gets all users image blobs
     */
    public PersonPropertiesResourceAvatar(final ActionController inServiceActionController,
            final ServiceAction inGetAllPersonAvatar, final ServiceAction inGetAllPersonAvatarId)
    {
        serviceActionController = inServiceActionController;
        getAllPersonAvatar = inGetAllPersonAvatar;
        getAllPersonAvatarId = inGetAllPersonAvatarId;
    }

    /**
     * {@inheritDoc}. Nothing needed here because there are no params to initialize.
     */
    @Override
    protected void initParams(final Request request)
    {
        urlJson = (String) request.getAttributes().get("urlJson");
    }
    /**
     * {@inheritDoc}. Retrieve the information from the actions and return back a json string that contains the
     * accountid, and additional properties map for each user in the db.
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {     
        List<Map<String, Object>> people;
        List<Map<String, Object>> avatars;
        String json;

        //parse the json input from the url
        try {
			json = URLDecoder.decode(urlJson,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}        
        JSONArray jsonArray = JSONArray.fromObject(json);
        
        //create a string to be based as a paramter to the db query
        ArrayList dbList = new ArrayList();
        for(Object jsonItem:jsonArray){
        	dbList.add(((JSONObject)jsonItem).get("id"));
        }
        String dbListStr = StringUtils.join(dbList,",");
        
        //get users from url list
        try
        {
            people = (ArrayList<Map<String, Object>>) serviceActionController.execute(
            		new ServiceActionContext(dbListStr, null), getAllPersonAvatar);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving the users from the db.", ex);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }
        
        //create new object to be passed back as json and create list of avatarIds to be passed to the db query
        JSONArray personProperties = new JSONArray();
        JSONObject personJs;
        ArrayList avatarIdList = new ArrayList();
        for (Map<String, Object> currentPersonProperties : people)
        {
			if(currentPersonProperties.get("avatarId")!=null)
	    	{
	        	avatarIdList.add(IMAGE_PREFIX+currentPersonProperties.get("avatarId"));
	    	}
	    	personJs=new JSONObject();
			personJs.put("id", (String) currentPersonProperties.get("accountId"));
			personJs.put("avatarId", (String) currentPersonProperties.get("avatarId"));
			personProperties.add(personJs);
        }
        String avatarIdListStr = StringUtils.join(avatarIdList,",");
        
        //get all avatars by avatarId
        try
        {
            avatars = (ArrayList<Map<String, Object>>) serviceActionController.execute(
            		new ServiceActionContext(avatarIdListStr, null), getAllPersonAvatarId);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving the users from the db.", ex);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }
        
        String avatarId;
        String imageId;
        boolean isAvatarSame;
        for (Map<String, Object> currentAvatar : avatars)
        {
        	isAvatarSame = false;
        	//check if avatar is the same
        	for(Object jsonItem:jsonArray){
        		if(((JSONObject) jsonItem).get("avatarId") != null && currentAvatar.get("imageIdentifier") != null)
        		{
	        		if(currentAvatar.get("imageIdentifier").equals(IMAGE_PREFIX+((JSONObject)jsonItem).get("avatarId")))
	        		{
	        			isAvatarSame = true;
	        		}
        		}
        	}
        	//if it's not the same send back base64 image
    		if(!isAvatarSame)
    		{
	        	for(Object obj : personProperties)
	        	{
	        		avatarId = IMAGE_PREFIX+((JSONObject)obj).get("avatarId");
	        		imageId = (String) currentAvatar.get("imageIdentifier");
	        		if(avatarId.equals(imageId))
	        		{
	        			((JSONObject)obj).put("imageBlob", Base64.encodeBase64String((byte[]) currentAvatar.get("imageBlob")));
	        		}
	        	}
    		}
        }
        
        Representation rep = new StringRepresentation(personProperties.toString(), MediaType.APPLICATION_JSON);
        rep.setExpirationDate(new Date(0L));
        
        return rep;
    }
}
