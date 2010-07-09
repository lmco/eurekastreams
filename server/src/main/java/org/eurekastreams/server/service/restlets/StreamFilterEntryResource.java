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
import java.util.LinkedList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.commons.formatting.DateFormatter;
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
public class StreamFilterEntryResource extends SmpResource
{
    /**
     * max number of activities.
     */
    private static final int MAXCOUNT = 5;
    /**
     * Open Social Id. TODO: this should be eliminated when we have OAuth.
     */
    private String openSocialId;

    /**
     * Id of the filter.
     */
    private Long id;
    /**
     * filter fetcher strategy.
     */
    private StreamFilterFetcher filterFetcher;

    /**
     * init the params.
     *
     * @param request
     *            the request object.
     */
    @Override
    protected void initParams(final Request request)
    {
        id = Long.valueOf((String) request.getAttributes().get("id"));
        openSocialId = (String) request.getAttributes().get("openSocialId");
    }

    /**
     *
     * The Job Mapper. Used by tests.
     *
     * @param inFilterFetcher
     *            filter fetecher strategy.
     */
    public void setFilterFetcher(final StreamFilterFetcher inFilterFetcher)
    {
        filterFetcher = inFilterFetcher;
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
    @Override
    public Representation represent(final Variant variant)
            throws ResourceException
    {
        PagedSet<ActivityDTO> activities;

        try
        {
            activities = filterFetcher.getActivities(id,
                    openSocialId, MAXCOUNT);
        }
        catch (ResourceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            activities = new PagedSet<ActivityDTO>();
            activities.setPagedSet(new LinkedList<ActivityDTO>());
        }

        DateFormatter dateFormatter = new DateFormatter(new Date());
        JSONObject json = new JSONObject();

        JSONArray jsonActivities = new JSONArray();
        for (ActivityDTO activity : activities.getPagedSet())
        {
            AvatarUrlGenerator actorUrlGen = new AvatarUrlGenerator(
                    EntityType.PERSON);

            JSONObject jsonActivity = new JSONObject();
            jsonActivity.put("commentCount", activity.getCommentCount());
            jsonActivity.put("destinationDisplayName", activity.getDestinationStream().getDisplayName());
            jsonActivity.put("destinationUniqueIdentifier",
                    activity.getDestinationStream().getUniqueIdentifier());
            jsonActivity.put("destinationType", activity.getDestinationStream().getType());
            jsonActivity.put("actorAvatarPath", actorUrlGen
                    .getSmallAvatarUrl(activity.getActor().getId(), activity
                            .getActor().getAvatarId()));
            jsonActivity.put("actorDisplayName", activity.getActor()
                    .getDisplayName());
            jsonActivity.put("actorUniqueIdentifier", activity.getActor()
                    .getUniqueIdentifier());
            jsonActivity.put("actorType", activity.getActor().getType());
            jsonActivity.put("verb", activity.getVerb());
            jsonActivity.put("postedTimeAgo", dateFormatter.timeAgo(activity.getPostedTime()));
            jsonActivity.put("baseObjectType", activity.getBaseObjectType()
                    .toString());
            jsonActivity.put("activityId", activity.getId());
            jsonActivity.put("originalActorAvatarPath", actorUrlGen
                    .getSmallAvatarUrl(activity.getOriginalActor().getId(),
                            activity.getOriginalActor().getAvatarId()));
            jsonActivity.put("originalActorActivityId", activity
                    .getOriginalActor().getAvatarId());
            jsonActivity.put("originalActorDisplayName", activity
                    .getOriginalActor().getDisplayName());
            jsonActivity.put("originalActorUniqueIdentifier", activity
                    .getOriginalActor().getUniqueIdentifier());
            jsonActivity.put("originalActorType", activity.getOriginalActor()
                    .getType());


            for (String key : activity.getBaseObjectProperties().keySet())
            {
                jsonActivity.put(key, activity.getBaseObjectProperties().get(
                        key));

            }
            jsonActivities.add(jsonActivity);
        }
        json.put("activities", jsonActivities);

        Representation rep = new StringRepresentation(json.toString(),
                MediaType.APPLICATION_JSON);
        rep.setExpirationDate(new Date(0L));

        return rep;
    }

}
