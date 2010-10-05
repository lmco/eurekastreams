/*
 * Copyright (c) 2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.action.execution.opensocial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.execution.stream.ActivitySecurityTrimmer;
import org.eurekastreams.server.action.request.opensocial.GetUserActivitiesRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * This Execution strategy is responsible for retrieving activities for the opensocial adapter. There are two ways to
 * get activities back from this strategy: - Pass in a list of activity ids and the corresponding ActivityDTO objects
 * will be returned. - Pass in just the user id and all of the activities that user posted to their own stream will be
 * returned.
 * 
 */
public class GetUserActivitiesExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local instance of the {@link BulkActivitiesMapper}.
     */
    private final DomainMapper<List<Long>, List<ActivityDTO>> bulkActivitiesMapper;

    /**
     * Local instance of the {@link GetPeopleByOpenSocialIds} mapper.
     */
    private final GetPeopleByOpenSocialIds getPeopleByOpenSocialIds;

    /**
     * Mapper to get activities by JSON request.
     */
    private final ExecutionStrategy<PrincipalActionContext> getActivitiesByRequestExecution;

    /**
     * Max number of activities to fetch by open social id.
     */
    private final Long maxActivitiesToReturnByOpenSocialId;

    /**
     * Security trimmer.
     */
    private final ActivitySecurityTrimmer securityTrimmer;

    /**
     * Constructor.
     * 
     * @param inBulkActivitiesMapper
     *            - instance of the {@link BulkActivitiesMapper}.
     * @param inGetPeopleByOpenSocialIds
     *            - instance of the {@link GetPeopleByOpenSocialIds} mapper.
     * @param inGetActivitiesByRequestExecution
     *            execution strategy to get activities by JSON
     * @param inMaxActivitiesToReturnByOpenSocialId
     *            the maximum number of activities to fetch by people open social ids
     * @param inSecurityTrimmer
     *            the security trimmer.
     */
    public GetUserActivitiesExecution(final DomainMapper<List<Long>, List<ActivityDTO>> inBulkActivitiesMapper,
            final GetPeopleByOpenSocialIds inGetPeopleByOpenSocialIds,
            final ExecutionStrategy<PrincipalActionContext> inGetActivitiesByRequestExecution,
            final Long inMaxActivitiesToReturnByOpenSocialId, final ActivitySecurityTrimmer inSecurityTrimmer)
    {
        bulkActivitiesMapper = inBulkActivitiesMapper;
        getPeopleByOpenSocialIds = inGetPeopleByOpenSocialIds;
        getActivitiesByRequestExecution = inGetActivitiesByRequestExecution;
        maxActivitiesToReturnByOpenSocialId = inMaxActivitiesToReturnByOpenSocialId;
        securityTrimmer = inSecurityTrimmer;
    }

    /**
     * {@inheritDoc}
     * 
     * This execute method retrieves the ActivityDTO objects for the parameters passed in.
     */
    @Override
    public LinkedList<ActivityDTO> execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        GetUserActivitiesRequest currentRequest = (GetUserActivitiesRequest) inActionContext.getParams();
        LinkedList<ActivityDTO> currentActivityDTOs = new LinkedList<ActivityDTO>();

        if (currentRequest.getOpenSocialIds() != null && currentRequest.getOpenSocialIds().size() > 0)
        {
            List<PersonModelView> users = getPeopleByOpenSocialIds.execute(new ArrayList<String>(currentRequest
                    .getOpenSocialIds()));

            if (users.size() > 0)
            {
                final JSONObject json = new JSONObject();
                json.put("count", maxActivitiesToReturnByOpenSocialId);

                JSONArray recipients = new JSONArray();
                for (PersonModelView user : users)
                {
                    JSONObject recipient = new JSONObject();
                    recipient.put("type", "PERSON");
                    recipient.put("name", user.getAccountId());

                    recipients.add(recipient);
                }
                JSONObject query = new JSONObject();
                query.put("recipient", recipients);
                json.put("query", query);

                PrincipalActionContext context = new PrincipalActionContext()
                {
                    @Override
                    public Principal getPrincipal()
                    {
                        return inActionContext.getPrincipal();
                    }

                    @Override
                    public String getActionId()
                    {
                        return null;
                    }

                    @Override
                    public Serializable getParams()
                    {
                        return json.toString();
                    }

                    @Override
                    public Map<String, Object> getState()
                    {
                        return null;
                    }

                    @Override
                    public void setActionId(final String inActionId)
                    {
                    }
                };

                PagedSet<ActivityDTO> activities = (PagedSet<ActivityDTO>) getActivitiesByRequestExecution
                        .execute(context);

                currentActivityDTOs.addAll(activities.getPagedSet());
            }
        }

        // if the user has provided activities to retrieve in the parameters, retrieve the corresponding
        // ActivityDTO objects.
        if (currentRequest.getActivityIds() != null && currentRequest.getActivityIds().size() > 0)
        {
            List<Long> activityIds = new ArrayList<Long>(currentRequest.getActivityIds());
            System.out.println("Before: " + activityIds.size());
            // only look for IDs that aren't yet in the list
            for (ActivityDTO act : currentActivityDTOs)
            {
                if (activityIds.contains(act.getId()))
                {
                    activityIds.remove(act.getId());
                }
            }

            activityIds = securityTrimmer.trim(activityIds, inActionContext.getPrincipal().getId());

            if (!activityIds.isEmpty())
            {
                currentActivityDTOs.addAll(bulkActivitiesMapper.execute(activityIds));
            }
        }

        return currentActivityDTOs;
    }
}
