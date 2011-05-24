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
import org.eurekastreams.server.domain.Followable;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Action to get the followers of a person.
 * 
 */
public class GetFollowingExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Logger. */
    private Log log = LogFactory.make();

    /** Mapper to find the follower's id given their account id. */
    private DomainMapper<String, Long> getPersonIdByAccountIdMapper;

    /** Mapper returning the ids of the entities being followed. */
    private DomainMapper<Long, List<Long>> personIdsMapper;

    /** Mapper returning the ids of the entities being followed. */
    private DomainMapper<Long, List<Long>> groupIdsMapper;

    /** Mapper returning entities (people, groups) given their ids. */
    private DomainMapper<List<Long>, List<Followable>> bulkPersonModelViewMapper;

    /** Mapper returning entities (people, groups) given their ids. */
    private DomainMapper<List<Long>, List<Followable>> bulkGroupModelViewMapper;

    /**
     * Constructor.
     * 
     * @param inGetPersonIdByAccountIdMapper
     *            Mapper to find the follower's id given their account id.
     * @param inPersonIdsMapper
     *            Mapper returning the ids of the entities being followed.
     * @param inGroupIdsMapper
     *            Mapper returning the ids of the entities being followed.
     * @param inBulkModelViewMapper
     *            Mapper returning entities (people, groups) given their ids.
     */
    public GetFollowingExecution(final DomainMapper<String, Long> inGetPersonIdByAccountIdMapper,
            final DomainMapper<Long, List<Long>> inPersonIdsMapper,
            final DomainMapper<Long, List<Long>> inGroupIdsMapper,
            final DomainMapper<List<Long>, List<Followable>> inBulkPersonModelViewMapper,
            final DomainMapper<List<Long>, List<Followable>> inBulkGroupModelViewMapper)
    {
        getPersonIdByAccountIdMapper = inGetPersonIdByAccountIdMapper;
        personIdsMapper = inPersonIdsMapper;
        groupIdsMapper = inGroupIdsMapper;
        bulkPersonModelViewMapper = inBulkPersonModelViewMapper;
        bulkGroupModelViewMapper = inBulkGroupModelViewMapper;
    }

    /**
     * Returns Set of people following a user excluding themselves.
     * 
     * @param inActionContext
     *            The action context.
     * @return true if the group exists and the user is authorized, false otherwise
     */
    @Override
    public PagedSet<Followable> execute(final PrincipalActionContext inActionContext)
    {
        // get the request
        GetFollowersFollowingRequest inRequest = (GetFollowersFollowingRequest) inActionContext.getParams();

        // get the unique entity Id
        final String entityUniqueId = inRequest.getEntityId();

        Long entityId = getPersonIdByAccountIdMapper.execute(entityUniqueId);

        List<Long> allPersonIds = personIdsMapper.execute(entityId);
        List<Long> allGroupIds = groupIdsMapper.execute(entityId);

        // determine the page
        int startIndex = ((Integer) inRequest.getStartIndex()).intValue();
        int endIndex = ((Integer) inRequest.getEndIndex()).intValue();

        PagedSet<Followable> pagedSet;
        if (allPersonIds.isEmpty() && allGroupIds.isEmpty())
        {
            pagedSet = new PagedSet<Followable>();
        }
        else if (startIndex >= (allPersonIds.size() + allGroupIds.size()))
        {
            // if asking for a range beyond the end of the list return an empty set
            pagedSet = new PagedSet<Followable>();
            pagedSet.setTotal(allPersonIds.size() + allGroupIds.size());
        }
        else
        {
            if (endIndex >= (allPersonIds.size() + allGroupIds.size()))
            {
                endIndex = allPersonIds.size() + allGroupIds.size() - 1;
            }
            List<Long> personIds = allPersonIds.subList(startIndex, endIndex + 1);
            List<Followable> list = bulkPersonModelViewMapper.execute(personIds);

            List<Long> groupIds = allGroupIds.subList(startIndex, endIndex + 1);
            list.addAll(bulkGroupModelViewMapper.execute(groupIds));

            pagedSet = new PagedSet<Followable>(startIndex, endIndex, allPersonIds.size() + allGroupIds.size(), list);
        }

        if (log.isTraceEnabled())
        {
            log.trace("Retrieved " + pagedSet.getFromIndex() + " to " + pagedSet.getToIndex() + " of "
                    + pagedSet.getTotal() + " following for person " + entityUniqueId);
        }

        return pagedSet;
    }

}
