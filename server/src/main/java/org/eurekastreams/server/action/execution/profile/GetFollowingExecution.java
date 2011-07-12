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
package org.eurekastreams.server.action.execution.profile;

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.FollowerStatusable;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.strategies.FollowerStatusPopulator;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Action to get the followers of a person.
 *
 */
public class GetFollowingExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Logger. */
    private final Log log = LogFactory.make();

    /** Mapper to find the follower's id given their account id. */
    private final DomainMapper<String, Long> getPersonIdByAccountIdMapper;

    /** Mapper returning the ids of the entities being followed. */
    private final DomainMapper<Long, List<Long>> idsMapper;

    /** Mapper returning entities (people, groups) given their ids. */
    private final DomainMapper<List<Long>, List<FollowerStatusable>> bulkModelViewMapper;

    /**
     * Populator for follower status of results.
     */
    private final FollowerStatusPopulator<FollowerStatusable> followerStatusPopulator;

    /**
     * Constructor.
     *
     * @param inGetPersonIdByAccountIdMapper
     *            Mapper to find the follower's id given their account id.
     * @param inIdsMapper
     *            Mapper returning the ids of the entities being followed.
     * @param inBulkModelViewMapper
     *            Mapper returning entities (people, groups) given their ids.
     * @param inFollowerStatusPopulator
     *            populates each PersonModelView with the current user's follow status
     */
    public GetFollowingExecution(final DomainMapper<String, Long> inGetPersonIdByAccountIdMapper,
            final DomainMapper<Long, List<Long>> inIdsMapper,
            final DomainMapper<List<Long>, List<FollowerStatusable>> inBulkModelViewMapper,
            final FollowerStatusPopulator<FollowerStatusable> inFollowerStatusPopulator)
    {
        getPersonIdByAccountIdMapper = inGetPersonIdByAccountIdMapper;
        idsMapper = inIdsMapper;
        bulkModelViewMapper = inBulkModelViewMapper;
        followerStatusPopulator = inFollowerStatusPopulator;
    }

    /**
     * Returns Set of people following a user excluding themselves.
     *
     * @param inActionContext
     *            The action context.
     * @return true if the group exists and the user is authorized, false otherwise
     */
    @Override
    public PagedSet<FollowerStatusable> execute(final PrincipalActionContext inActionContext)
    {
        // get the request
        GetFollowersFollowingRequest inRequest = (GetFollowersFollowingRequest) inActionContext.getParams();

        // get the unique entity Id
        final String entityUniqueId = inRequest.getEntityId();

        final long currentUserId = inActionContext.getPrincipal().getId();

        Long entityId = getPersonIdByAccountIdMapper.execute(entityUniqueId);

        List<Long> allIds = idsMapper.execute(entityId);

        // determine the page
        int startIndex = (inRequest.getStartIndex()).intValue();
        int endIndex = (inRequest.getEndIndex()).intValue();

        PagedSet<FollowerStatusable> pagedSet;
        if (allIds.isEmpty())
        {
            pagedSet = new PagedSet<FollowerStatusable>();
        }
        else if (startIndex >= allIds.size())
        {
            // if asking for a range beyond the end of the list return an empty set
            pagedSet = new PagedSet<FollowerStatusable>();
            pagedSet.setTotal(allIds.size());
        }
        else
        {
            if (endIndex >= allIds.size())
            {
                endIndex = allIds.size() - 1;
            }
            List<Long> pageIds = allIds.subList(startIndex, endIndex + 1);

            List<FollowerStatusable> list = bulkModelViewMapper.execute(pageIds);
            followerStatusPopulator.execute(currentUserId, list, FollowerStatus.NOTSPECIFIED);

            pagedSet = new PagedSet<FollowerStatusable>(startIndex, endIndex, allIds.size(), list);
        }

        if (log.isTraceEnabled())
        {
            log.trace("Retrieved " + pagedSet.getFromIndex() + " to " + pagedSet.getToIndex() + " of "
                    + pagedSet.getTotal() + " following for person " + entityUniqueId);
        }

        return pagedSet;
    }

}
