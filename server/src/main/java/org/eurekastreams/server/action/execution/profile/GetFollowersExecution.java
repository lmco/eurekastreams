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

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.OrganizationChild;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.FollowMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.cache.PopulateOrgChildWithSkeletonParentOrgsCacheMapper;

/**
 * Action to get the followers of a person or group or the people or groups followed by a person.
 *
 */
public class GetFollowersExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
	private Log log = LogFactory.make();

    /**
     * Mapper to populate OrganizationChildren's parentOrganization with skeleton orgs from cache.
     */
    private PopulateOrgChildWithSkeletonParentOrgsCacheMapper orgChildrenSkeletonParentOrgPopulatorCacheMapper;

    /**
     * PersonMapper used to retrieve person from the db.
     */
    private PersonMapper personMapper = null;

    /**
     * GroupMapper used to retrieve person from the db.
     */
    private DomainGroupMapper groupMapper = null;

    /**
     * Constructor that sets up the mapper.
     *
     * @param inPersonMapper
     *            - instance of PersonMapper
     * @param inGroupMapper
     *            - instance of DomainGroupMapper
     * @param inOrgChildrenSkeletonParentOrgPopulatorCacheMapper
     *            mapper to populate the followers' parent orgs with skeleton orgs from cache
     */
    public GetFollowersExecution(final PersonMapper inPersonMapper, final DomainGroupMapper inGroupMapper,
            final PopulateOrgChildWithSkeletonParentOrgsCacheMapper inOrgChildrenSkeletonParentOrgPopulatorCacheMapper)
    {
        personMapper = inPersonMapper;
        groupMapper = inGroupMapper;
        orgChildrenSkeletonParentOrgPopulatorCacheMapper = inOrgChildrenSkeletonParentOrgPopulatorCacheMapper;
    }


    /**
     * Returns true or false if the group exists and the current user is a coordinator.
     *
     * @param inActionContext
     *            The action context.
     * @return true if the group exists and the user is authorized, false otherwise
     */
    @Override
    public PagedSet<Person> execute(final PrincipalActionContext inActionContext)
    {
        // get the request
        GetFollowersFollowingRequest inRequest = (GetFollowersFollowingRequest) inActionContext.getParams();

        // get the unique entity Id
        final String uniqueEntityId = inRequest.getEntityId();

        // get the entity type.
        EntityType targetType = inRequest.getEntityType();

        // get the start value.
        Integer startValue = (inRequest.getStartIndex()).intValue();

        // get the end value.
        Integer endValue = (inRequest.getEndIndex()).intValue();

        // given the entity type, get the follow mapper.
        FollowMapper mapper = pickMapper(targetType);

        PagedSet<Person> connections =
        	mapper.getFollowers(uniqueEntityId, startValue, endValue);

        // load the org children followers with skeleton parent orgs
        orgChildrenSkeletonParentOrgPopulatorCacheMapper.populateParentOrgSkeletons(new ArrayList<OrganizationChild>(
                connections.getPagedSet()));

        if (log.isTraceEnabled())
        {
            log.trace("Retrieved " + connections.getFromIndex() + " to " + connections.getToIndex() + " of "
                    + connections.getTotal() + " followers");
        }

        return connections;
    }

    /**
     * Pick a mapper based on the type of the target.
     *
     * @param targetType
     *            the target type
     * @return the selected mapper
     */
    private FollowMapper pickMapper(final EntityType targetType)
    {
        if (EntityType.PERSON == targetType)
        {
            return personMapper;
        }
        if (EntityType.GROUP == targetType)
        {
            return groupMapper;
        }

        throw new IllegalArgumentException("GetFollowersAction supports Person and DomainGroup targets only");
    }
}
