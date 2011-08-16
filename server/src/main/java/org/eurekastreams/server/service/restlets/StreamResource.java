/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.service.restlets.support.RestletQueryRequestParser;
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
    private final Log log = LogFactory.getLog(StreamResource.class);

    /**
     * Open Social Id. TODO: this should be eliminated when we have OAuth.
     */
    private String openSocialId;

    /**
     * Action.
     */
    private final ServiceAction action;

    /**
     * Service Action Controller.
     */
    private final ActionController serviceActionController;

    /**
     * Principal populator.
     */
    private final PrincipalPopulator principalPopulator;

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
     * Stream find by ID mapper.
     */
    private FindByIdMapper<Stream> streamMapper = null;

    /**
     * The mode of the resource, can be ad-hoc query, or saved stream. "query" is for ad-hoc. "saved" is for saved
     * stream.
     */
    private String mode;

    /**
     * The stream Id.
     */
    private long streamId;

    /** Extracts the query out of the request path. */
    private final RestletQueryRequestParser requestParser;

    /**
     * Default constructor.
     *
     * @param inAction
     *            the action.
     * @param inServiceActionController
     *            {@link ActionController} used to execute action.
     * @param inPrincipalPopulator
     *            {@link PrincipalPopulator} used to create principal via open social id.
     * @param inStreamMapper
     *            the stream mapper.
     * @param inRequestParser
     *            Extracts the query out of the request path.
     */
    public StreamResource(final ServiceAction inAction, final ActionController inServiceActionController,
            final PrincipalPopulator inPrincipalPopulator, final FindByIdMapper<Stream> inStreamMapper,
            final RestletQueryRequestParser inRequestParser)
    {
        action = inAction;
        serviceActionController = inServiceActionController;
        principalPopulator = inPrincipalPopulator;
        streamMapper = inStreamMapper;
        requestParser = inRequestParser;
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
        openSocialId = (String) request.getAttributes().get("openSocialId");
        callback = (String) request.getAttributes().get("callback");
        mode = (String) request.getAttributes().get("mode");
        String streamIdStr = ((String) request.getAttributes().get("streamId"));

        if (null != streamIdStr && mode.equals("saved"))
        {
            streamId = Long.parseLong(streamIdStr);
        }
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
        if (log.isDebugEnabled())
        {
            log.debug("Path: " + getPath());
        }

        JSONObject responseJson = new JSONObject();
        String status = GOOD_STATUS;

        try
        {
            JSONObject queryJson = null;

            if (mode.equals("query"))
            {
                int start = (null == callback) ? 5 : 7;
                queryJson = requestParser.parseRequest(getPath(), start);
            }
            else if (mode.equals("saved"))
            {
                Stream stream = streamMapper.execute(new FindByIdRequest("Stream", streamId));
                if (stream == null)
                {
                    throw new Exception("Unknown saved stream.");
                }
                queryJson = JSONObject.fromObject(stream.getRequest());
            }
            else
            {
                throw new Exception("Unknown request mode.");
            }

            if (log.isDebugEnabled())
            {
                log.debug("Making request using: " + queryJson);
            }

            PrincipalActionContext ac = new ServiceActionContext(queryJson.toString(),
                    principalPopulator.getPrincipal(openSocialId, ""));
            PagedSet<ActivityDTO> activities = (PagedSet<ActivityDTO>) serviceActionController.execute(
                    ac, action);

            responseJson.put("query", queryJson.getJSONObject("query"));

            DateFormatter dateFormatter = new DateFormatter(new Date());
            JSONArray jsonActivities = new JSONArray();
            for (ActivityDTO activity : activities.getPagedSet())
            {
                AvatarUrlGenerator actorUrlGen = new AvatarUrlGenerator(EntityType.PERSON);

                JSONObject jsonActivity = new JSONObject();
                jsonActivity.put("commentCount", activity.getCommentCount());
                jsonActivity.put("likeCount", activity.getLikeCount());
                jsonActivity.put("destinationDisplayName", activity.getDestinationStream().getDisplayName());
                jsonActivity.put("destinationUniqueIdentifier", activity.getDestinationStream().getUniqueIdentifier());
                jsonActivity.put("destinationType", activity.getDestinationStream().getType());
                jsonActivity.put("actorAvatarPath", actorUrlGen.getSmallAvatarUrl(activity.getActor().getAvatarId()));
                jsonActivity.put("actorDisplayName", activity.getActor().getDisplayName());
                jsonActivity.put("actorUniqueIdentifier", activity.getActor().getUniqueIdentifier());
                jsonActivity.put("actorType", activity.getActor().getType());
                jsonActivity.put("verb", activity.getVerb());
                jsonActivity.put("postedTimeAgo", dateFormatter.timeAgo(activity.getPostedTime()));
                jsonActivity.put("baseObjectType", activity.getBaseObjectType().toString());
                jsonActivity.put("activityId", activity.getId());
                jsonActivity.put("originalActorAvatarPath",
                        actorUrlGen.getSmallAvatarUrl(activity.getOriginalActor().getAvatarId()));
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

            responseJson.put("activities", jsonActivities);

        }
        catch (Exception ex)
        {
            status = "Error: " + ex.toString();
        }

        log.debug(status);
        responseJson.put("status", status);

        String jsString = responseJson.toString();

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

}
