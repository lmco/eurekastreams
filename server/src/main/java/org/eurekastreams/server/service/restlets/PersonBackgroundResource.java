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
/**
 * 
 */
package org.eurekastreams.server.service.restlets;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

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
import org.eurekastreams.server.domain.Background;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.persistence.BackgroundMapper;
import org.eurekastreams.commons.exceptions.ValidationException;

/**
 *
 */
public class PersonBackgroundResource extends WritableResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(PersonBackgroundResource.class);

    /**
     * Mapper to retrieve a Background.
     */
    private BackgroundMapper mapper;

    /**
     * The UUID of the person whose background is being requested.
     */
    private String uuid;

    /**
     * The key used in the JSON string.
     */
    public static final String MAIN_KEY = "background";

    /**
     * The key used to identify the person's account id.
     */
    public static final String UUID_KEY = "uuid";
    
    /**
     * The json key to identify the affiliations object.
     */
    public static final String AFFILIATIONS_KEY = "affiliations";
    
    /**
     * The json key to identify the honors and awards object.
     */
    public static final String HONORSAWARDS_KEY = "honorsawards";
    
    /**
     * The json key to identify the interests and hobbies object.
     */
    public static final String INTERESTSHOBBIES_KEY = "interestshobbies";
    
    /**
     * The json key to identify the skills and specialties object.
     */
    public static final String SKILLSSPECIALTIES_KEY = "skillsspecialties";
    
    /**
     * Map of BackgroundItemTypes applicable to this restlet.
     */
    private static Map<String, BackgroundItemType> itemTypes;

    /**
     * Pattern for input validation. Allows zero or more alphanumeric , spaces, and .,-#!@$%^&*()'
     */
    private static final String REGEXP_PATTERN = 
            "[(a-zA-Z0-9\\s\\.,\\-\\#\\(\\)\\!\\@\\$\\%\\^\\&\\*\\')]*";
    
    /**
     * Initialize parameters from the request object.
     *            the context of the request
     * @param request
     *            the client's request
     */
    protected void initParams(final Request request)
    {
        uuid = (String) request.getAttributes().get(UUID_KEY);
        log.debug("Building Resource for " + uuid);
        
        //Setup the keys for the json object
        itemTypes = new HashMap<String, BackgroundItemType>();
        itemTypes.put(AFFILIATIONS_KEY, BackgroundItemType.AFFILIATION);
        itemTypes.put(HONORSAWARDS_KEY, BackgroundItemType.HONOR);
        itemTypes.put(INTERESTSHOBBIES_KEY, BackgroundItemType.INTEREST);
        itemTypes.put(SKILLSSPECIALTIES_KEY, BackgroundItemType.SKILL);
    }

    /**
     * This method is only here to help in testing. I want to find a better way to get spring involved in the setup, and
     * to have spring inject a mocked mapper.
     * 
     * @param inMapper
     *            mapper.
     */
    public void setMapper(final BackgroundMapper inMapper)
    {
        mapper = inMapper;
    }

    /**
     * Handles GET request.
     * 
     * @param variant
     *            the variant whose representation must be returned
     * @return representation of the org description
     * @throws ResourceException
     *             on error
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        Background bg = mapper.findOrCreatePersonBackground(uuid);
        JSONObject bgAsJson = convertBackgroundToJSON(bg);

        String json = bgAsJson.toString();
        log.debug("PersonBackgroundResource accepted json for uuid: " + uuid);
        log.debug("PersonBackgroundResource constructed json: " + json);
        
        Representation rep = new StringRepresentation(json, MediaType.APPLICATION_JSON); 
        
        rep.setExpirationDate(new Date(0L));
        
        return rep;

    }

    /**
     * Validate a given JSONArray.
     * @param inArray - JSONArray that contains the background items to validate.
     * @param inBgType - BackgroundItemType of the items being validated.
     */
    private void validateJSONArray(final JSONArray inArray, 
            final BackgroundItemType inBgType)
    {
        for (Object token : inArray)
        {
           if (!Pattern.matches(REGEXP_PATTERN, (String) token))
           {
               String msg = inBgType.name() + ": " + (String) token + " contains invalid characters.  "
               + "Valid characters include alphanumeric, space, (!@$%^&*#-.,')";
               log.error(msg);
               JSONObject validationErrors = new JSONObject();
               validationErrors.put(VALIDATION_ERRORS_KEY, msg);
               getAdaptedResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
               getAdaptedResponse().setEntity(validationErrors.toString(), MediaType.APPLICATION_JSON);
               throw new ValidationException();
           }
        }
    }
    /**
     * Convert a given JSONArray to a list of BackgroundItems and add them to the supplied Background
     * Collection.
     * @param inArray - JSONArray that contains the background items to add to the list.
     * @param inBg - Background collection to add the JSONArray items to.
     * @param inBgType - BackgroundItemType of the items being added to the collection.
     */
    private void convertJSONArrayToBackgroundItems(final JSONArray inArray, 
            final Background inBg, final BackgroundItemType inBgType)
    {
        List<BackgroundItem> items = new ArrayList<BackgroundItem>(inArray.size());
        for (Object token : inArray)
        {
            items.add(new BackgroundItem((String) token, inBgType));
        }
        inBg.setBackgroundItems(items, inBgType);
    }
    
    /**
     * Clear out the database for this particular type of background item.  Before saving changed BackgroundItems
     * to the db, this clearing is necessary to avoid orphaned Background Items.
     * @param inBg - Background Collection to remove the items from.
     * @param inBgType - BackgroundItemType of the BackgroundItems to remove.
     */
    private void clearBackgroundItemsByType(final Background inBg, final BackgroundItemType inBgType)
    {    
        for (BackgroundItem item : inBg.getBackgroundItems(inBgType))
        {
            mapper.deleteItem(item);
        }
    }
    
    /**
     * Handle PUT requests.
     * 
     * @param entity
     *            the resource's new representation
     */
    @Override
    public void storeRepresentation(final Representation entity)
    {
        Background bg = mapper.findOrCreatePersonBackground(uuid);

        try
        {
            String jsontext = entity.getText();
            log.debug("PersonBackgroundResource accepted json for uuid: " + uuid);
            log.debug("PersonBackgroundResource is parsing json: " + jsontext);
            
            JSONObject jsonObject = JSONObject.fromObject(jsontext);
            if (jsonObject.containsKey(AFFILIATIONS_KEY) 
                    && jsonObject.containsKey(HONORSAWARDS_KEY)
                    && jsonObject.containsKey(INTERESTSHOBBIES_KEY)
                    && jsonObject.containsKey(SKILLSSPECIALTIES_KEY))
            {
                validateJSONArray(jsonObject.getJSONArray(AFFILIATIONS_KEY), 
                        BackgroundItemType.AFFILIATION);
                clearBackgroundItemsByType(bg, BackgroundItemType.AFFILIATION);    
                convertJSONArrayToBackgroundItems(
                        jsonObject.getJSONArray(AFFILIATIONS_KEY),
                        bg, BackgroundItemType.AFFILIATION);
                
                validateJSONArray(jsonObject.getJSONArray(HONORSAWARDS_KEY), 
                        BackgroundItemType.HONOR);
                clearBackgroundItemsByType(bg, BackgroundItemType.HONOR);
                convertJSONArrayToBackgroundItems(
                        jsonObject.getJSONArray(HONORSAWARDS_KEY),
                        bg, BackgroundItemType.HONOR);
                
                validateJSONArray(jsonObject.getJSONArray(INTERESTSHOBBIES_KEY), 
                        BackgroundItemType.INTEREST);
                clearBackgroundItemsByType(bg, BackgroundItemType.INTEREST);
                convertJSONArrayToBackgroundItems(
                        jsonObject.getJSONArray(INTERESTSHOBBIES_KEY),
                        bg, BackgroundItemType.INTEREST);
                
                validateJSONArray(jsonObject.getJSONArray(SKILLSSPECIALTIES_KEY), 
                        BackgroundItemType.SKILL);
                clearBackgroundItemsByType(bg, BackgroundItemType.SKILL);
                convertJSONArrayToBackgroundItems(
                        jsonObject.getJSONArray(SKILLSSPECIALTIES_KEY),
                        bg, BackgroundItemType.SKILL);
                
                mapper.flush(uuid);
            }
            else
            {
                String msg = "Dataset returned for background items " 
                    + "does not contain all of the necessary JSONArrays.";
                log.info(msg);
                JSONObject validationErrors = new JSONObject();
                validationErrors.put(VALIDATION_ERRORS_KEY, msg);
                
                getAdaptedResponse().setEntity(validationErrors.toString(), MediaType.APPLICATION_JSON);
                getAdaptedResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        }
        catch (ValidationException vex)
        {
            log.debug("Validation exception for " + uuid + " passing response back to caller");
        }
        catch (Exception e)
        {
            String msg = "Unable to persist background items for "
                + uuid;
            log.error(msg, e);

            getAdaptedResponse().setEntity(msg, MediaType.TEXT_PLAIN);
            getAdaptedResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
    }
    
    /**
     * @param bg object to convert to json.
     * @return json object representing the bg you specified.
     */
    public JSONObject convertBackgroundToJSON(final Background bg)
    {
        JSONObject bgAsJson = new JSONObject();
        JSONArray bgItemsAsJson;
        
        // if bg is ever null here,
        // there's a bug in mapper.findOrCreatePersonBackground()
        
        for (Entry<String, BackgroundItemType> type : itemTypes.entrySet())
        {   
            bgItemsAsJson = new JSONArray();
            for (BackgroundItem bgItem : bg.getBackgroundItems(type.getValue()))
            {
                bgItemsAsJson.add(bgItem.toString());
            }
            bgAsJson.put(type.getKey(), bgItemsAsJson);
        }
        return bgAsJson;
    }

    /**
     * works, but commented out to indicate it's not being used.
     * 
     * This seemed like a handy method to have and if it works I hate to just delete it.
     * We might need it later, and it's not hurting anybody just sitting here.
     * Still, it's commented out code and isn't being used.
     * 
     * After some discussion, we decided to leave it in with a comment
     * that it seemed like decent work that didn't need to be thrown away.
     * 
     * 
     * @param bg
     * @return
     * @throws ResourceException 
     */
    /*
    public Background convertJSONToBackground(JSONObject jsonObject) throws ResourceException
    {

        CollectionFormat formatter = new CollectionFormat();
        
        Background bg = new Background(new Person());
        
        JSONArray sections = jsonObject.getJSONArray(PersonBackgroundResource.MAIN_KEY);
        for (Object section : sections)
        {
            JSONObject object = (JSONObject) section;
            String title = (String) object.get("title");
            String text = (String) object.get("text");

            // convert user-friendly name to enum
            BackgroundItemType type = BackgroundItemType.toEnum(title);
            if (null == type)
            {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Found invalid title: " + title);
            }
            
            // convert the comma delimited string to background items
            // and add the newly created items to the Background
            Collection<String> elements = formatter.parse(text);
            List<BackgroundItem> backgroundItems = new ArrayList<BackgroundItem>();
            for (String token : elements)
            {
                backgroundItems.add(new BackgroundItem(token, type));
            }
            bg.setBackgroundItems(backgroundItems, type);
        }
        
        return bg;
       
    }
     */
}
