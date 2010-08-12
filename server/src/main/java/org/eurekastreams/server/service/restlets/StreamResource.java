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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
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
     * Default constructor.
     * 
     * @param inAction
     *            the action.
     * @param inServiceActionController
     *            {@link ServiceActionController} used to execute action.
     * @param inPrincipalPopulator
     *            {@link PrincipalPopulator} used to create principal via open social id.
     */
    @SuppressWarnings("unchecked")
    public StreamResource(final ServiceAction inAction, final ServiceActionController inServiceActionController,
            final PrincipalPopulator inPrincipalPopulator)
    {
        action = inAction;
        serviceActionController = inServiceActionController;
        principalPopulator = inPrincipalPopulator;
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
        String decodedJson;
        try
        {
            decodedJson = URLDecoder.decode(jsonRequest, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_EXPECTATION_FAILED,
                    "Unable to parse JSON Request from URL.");
        }

        // Create the actionContext
        PrincipalActionContext ac = 
            new ServiceActionContext(decodedJson, principalPopulator.getPrincipal(openSocialId));

        PagedSet<ActivityDTO> activities = (PagedSet<ActivityDTO>) serviceActionController.execute(
                (ServiceActionContext) ac, action);

        DateFormatter dateFormatter = new DateFormatter(new Date());
        JSONObject json = new JSONObject();

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

}
