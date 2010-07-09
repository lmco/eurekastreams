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
import org.eurekastreams.server.persistence.mappers.stream.GetItemsByPointerIds;

/**
 * Action to get the followers of a person.
 *
 */
public class GetFollowingExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Logger. */
    private Log log = LogFactory.make();

    /** Mapper to find the follower's id given their account id. */
    private GetItemsByPointerIds uniqueIdToIdLookupMapper;

    /** Mapper returning the ids of the entities being followed. */
    private DomainMapper<Long, List<Long>> idsMapper;

    /** Mapper returning entities (people, groups) given their ids. */
    private DomainMapper<List<Long>, List<Followable>> bulkModelViewMapper;

    /**
     * Constructor.
     *
     * @param inPersonAccountIdToIdLookupMapper
     *            Mapper to find the follower's id given their account id.
     * @param inIdsMapper
     *            Mapper returning the ids of the entities being followed.
     * @param inBulkModelViewMapper
     *            Mapper returning entities (people, groups) given their ids.
     */
    public GetFollowingExecution(final GetItemsByPointerIds inPersonAccountIdToIdLookupMapper,
            final DomainMapper<Long, List<Long>> inIdsMapper,
            final DomainMapper<List<Long>, List<Followable>> inBulkModelViewMapper)
    {
        uniqueIdToIdLookupMapper = inPersonAccountIdToIdLookupMapper;
        idsMapper = inIdsMapper;
        bulkModelViewMapper = inBulkModelViewMapper;
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

        Long entityId = uniqueIdToIdLookupMapper.fetchId(entityUniqueId);

        List<Long> allIds = idsMapper.execute(entityId);

        // determine the page
        int startIndex = ((Integer) inRequest.getStartIndex()).intValue();
        int endIndex = ((Integer) inRequest.getEndIndex()).intValue();

        PagedSet<Followable> pagedSet;
        if (allIds.isEmpty())
        {
            pagedSet = new PagedSet<Followable>();
        }
        else if (startIndex >= allIds.size())
        {
            // if asking for a range beyond the end of the list return an empty set
            pagedSet = new PagedSet<Followable>();
            pagedSet.setTotal(allIds.size());
        }
        else
        {
            if (endIndex >= allIds.size())
            {
                endIndex = allIds.size() - 1;
            }
            List<Long> pageIds = allIds.subList(startIndex, endIndex + 1);

            List<Followable> list = bulkModelViewMapper.execute(pageIds);

            pagedSet = new PagedSet<Followable>(startIndex, endIndex, allIds.size(), list);
        }

        if (log.isTraceEnabled())
        {
            log.trace("Retrieved " + pagedSet.getFromIndex() + " to " + pagedSet.getToIndex() + " of "
                    + pagedSet.getTotal() + " following for person " + entityUniqueId);
        }

        return pagedSet;
    }

}
