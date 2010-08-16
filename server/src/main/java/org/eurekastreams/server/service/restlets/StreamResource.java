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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * REST end point for stream filters.
 * 
 */
public class StreamResource extends SmpResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(TitlesCollectionResource.class);

    /**
     * Open Social Id. TODO: this should be eliminated when we have OAuth.
     */
    private String openSocialId;

    /**
     * JSON.
     */
    private String jsonRequest;

    /**
     * Action.
     */
    private ServiceAction action;

    /**
     * Service Action Controller.
     */
    private ServiceActionController serviceActionController;

    /**
     * Principal populator.
     */
    private PrincipalPopulator principalPopulator;

    /**
     * JSONP Callback.
     */
    private String callback;

    /**
     * Used for testing.
     */
    private String pathOverride;

    /**
     * Good status.
     */
    private static final String GOOD_STATUS = "OK";

    /**
     * Global keywords.
     */
    private List<String> globalKeywords = null;

    /**
     * Multiple entity keywords.
     */
    private List<String> multipleEntityKeywords = null;

    /**
     * Keywords.
     */
    private List<String> keywords = null;

    /**
     * Default constructor.
     * 
     * @param inAction
     *            the action.
     * @param inServiceActionController
     *            {@link ServiceActionController} used to execute action.
     * @param inPrincipalPopulator
     *            {@link PrincipalPopulator} used to create principal via open social id.
     * @param inGlobalKeywords
     *            the global keywords.
     * @param inMultipleEntityKeywords
     *            the multiple entities keyword.
     * @param inKeywords
     *            the other keywords.
     */
    @SuppressWarnings("unchecked")
    public StreamResource(final ServiceAction inAction, final ServiceActionController inServiceActionController,
            final PrincipalPopulator inPrincipalPopulator, final List<String> inGlobalKeywords,
            final List<String> inMultipleEntityKeywords, final List<String> inKeywords)
    {
        action = inAction;
        serviceActionController = inServiceActionController;
        principalPopulator = inPrincipalPopulator;
        globalKeywords = inGlobalKeywords;
        multipleEntityKeywords = inMultipleEntityKeywords;
        keywords = inKeywords;
    }

    /**
     * init the params.
     * 
     * @param request
     *            the request object.
     */
    @Override
    protected void initParams(final Request request)
    {
        jsonRequest = (String) request.getAttributes().get("json");
        openSocialId = (String) request.getAttributes().get("openSocialId");
        callback = (String) request.getAttributes().get("callback");
    }

    /**
     * GET the activites.
     * 
     * @param variant
     *            the variant.
     * @return the JSON.
     * @throws ResourceException
     *             the exception.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        log.debug("Path: " + getPath());

        String status = GOOD_STATUS;

        JSONObject decodedJson = null;

        try
        {
            decodedJson = parseRequest(getPath());
        }
        catch (Exception e)
        {
            status = "Error: " + e;
        }

        JSONObject json = new JSONObject();

        PagedSet<ActivityDTO> activities = null;

        if (GOOD_STATUS.equals(status))
        {
            log.debug("Making request using: " + decodedJson);
            json.put("query", decodedJson.getJSONObject("query"));

            // Create the actionContext
            PrincipalActionContext ac = new ServiceActionContext(decodedJson.toString(), principalPopulator
                    .getPrincipal(openSocialId));

            try
            {
                activities = (PagedSet<ActivityDTO>) serviceActionController.execute((ServiceActionContext) ac, action);
            }
            catch (Exception e)
            {
                status = "Error: " + e.toString();
            }

        }

        if (GOOD_STATUS.equals(status))
        {
            DateFormatter dateFormatter = new DateFormatter(new Date());

            JSONArray jsonActivities = new JSONArray();
            for (ActivityDTO activity : activities.getPagedSet())
            {
                AvatarUrlGenerator actorUrlGen = new AvatarUrlGenerator(EntityType.PERSON);

                JSONObject jsonActivity = new JSONObject();
                jsonActivity.put("commentCount", activity.getCommentCount());
                jsonActivity.put("destinationDisplayName", activity.getDestinationStream().getDisplayName());
                jsonActivity.put("destinationUniqueIdentifier", activity.getDestinationStream().getUniqueIdentifier());
                jsonActivity.put("destinationType", activity.getDestinationStream().getType());
                jsonActivity.put("actorAvatarPath", actorUrlGen.getSmallAvatarUrl(activity.getActor().getId(), activity
                        .getActor().getAvatarId()));
                jsonActivity.put("actorDisplayName", activity.getActor().getDisplayName());
                jsonActivity.put("actorUniqueIdentifier", activity.getActor().getUniqueIdentifier());
                jsonActivity.put("actorType", activity.getActor().getType());
                jsonActivity.put("verb", activity.getVerb());
                jsonActivity.put("postedTimeAgo", dateFormatter.timeAgo(activity.getPostedTime()));
                jsonActivity.put("baseObjectType", activity.getBaseObjectType().toString());
                jsonActivity.put("activityId", activity.getId());
                jsonActivity.put("originalActorAvatarPath", actorUrlGen.getSmallAvatarUrl(activity.getOriginalActor()
                        .getId(), activity.getOriginalActor().getAvatarId()));
                jsonActivity.put("originalActorActivityId", activity.getOriginalActor().getAvatarId());
                jsonActivity.put("originalActorDisplayName", activity.getOriginalActor().getDisplayName());
                jsonActivity.put("originalActorUniqueIdentifier", activity.getOriginalActor().getUniqueIdentifier());
                jsonActivity.put("originalActorType", activity.getOriginalActor().getType());

                for (String key : activity.getBaseObjectProperties().keySet())
                {
                    jsonActivity.put(key, activity.getBaseObjectProperties().get(key));

                }
                jsonActivities.add(jsonActivity);
            }

            json.put("activities", jsonActivities);
        }

        json.put("status", status);

        String jsString = json.toString();

        // JSONP
        if (null != callback)
        {
            jsString = callback + "(" + jsString + ")";
        }

        Representation rep = new StringRepresentation(jsString, MediaType.TEXT_PLAIN);
        rep.setExpirationDate(new Date(0L));

        return rep;
    }

    /**
     * Parses the request.
     * 
     * @param path
     *            the path.
     * @return the request.
     * @throws UnsupportedEncodingException
     *             thrown for bad request.
     */
    public JSONObject parseRequest(final String path) throws UnsupportedEncodingException
    {
        JSONObject json = new JSONObject();
        JSONObject query = new JSONObject();

        String[] parts = Pattern.compile("/").split(path);

        int start = (null == callback) ? 5 : 7;

        for (int i = start; i < parts.length - 1; i += 2)
        {
            log.debug("Found key: " + parts[i]);

            if (isMultipleEntityKeyword(parts[i]))
            {
                query.accumulate(parts[i], parseEntities(parts[i + 1]));
            }
            else if (isGlobalKeyword(parts[i]))
            {
                json.accumulate(parts[i], parts[i + 1]);
            }
            else if (isKeyword(parts[i]))
            {
                query.accumulate(parts[i], URLDecoder.decode(parts[i + 1], "UTF-8"));
            }
            else
            {
                throw new ValidationException("Unable to parse request, unrecognized keyword: " + parts[i]);
            }
        }

        json.accumulate("query", query);

        return json;
    }

    /**
     * Determine if the keyword is a multiple entity word.
     * 
     * @param keyword
     *            the word.
     * @return true or false.
     */
    public boolean isMultipleEntityKeyword(final String keyword)
    {
        return multipleEntityKeywords.contains(keyword);
    }

    /**
     * Determine if the keyword is a global word.
     * 
     * @param keyword
     *            the word.
     * @return true or false.
     */
    public boolean isGlobalKeyword(final String keyword)
    {
        return globalKeywords.contains(keyword);
    }

    /**
     * Determine if the keyword is recognized..
     * 
     * @param keyword
     *            the word.
     * @return true or false.
     */
    public boolean isKeyword(final String keyword)
    {
        return keywords.contains(keyword);
    }

    /**
     * Overrides the path.
     * 
     * @param inPathOverride
     *            the string to override the path with.
     */
    public void setPathOverride(final String inPathOverride)
    {
        pathOverride = inPathOverride;
    }

    /**
     * Get the path.
     * 
     * @return the path.
     */
    public String getPath()
    {
        if (pathOverride == null)
        {
            return getRequest().getResourceRef().getPath();
        }
        else
        {
            return pathOverride;
        }
    }

    /**
     * Parses entities from the request.
     * 
     * @param entityString
     *            the request string.
     * @return the entities.
     */
    private JSONArray parseEntities(final String entityString)
    {
        JSONArray entityArr = new JSONArray();
        String[] parts = Pattern.compile(",").split(entityString);

        for (String part : parts)
        {
            String[] entity = part.split(":");

            JSONObject entityObj = new JSONObject();
            entityObj.accumulate("name", entity[1]);
            entityObj.accumulate("type", entity[0]);

            entityArr.add(entityObj);
        }

        return entityArr;
    }
}
