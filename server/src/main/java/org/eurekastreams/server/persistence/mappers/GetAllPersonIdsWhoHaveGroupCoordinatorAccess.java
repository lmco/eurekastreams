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
package org.eurekastreams.server.persistence.mappers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.action.authorization.CoordinatorAccessAuthorizer;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Mapper to get a Set of IDs of all of the organizations recursively above the input org id, loading from cache if
 * possible, from DB if not, then populating the cache.
 */
public class GetAllPersonIdsWhoHaveGroupCoordinatorAccess implements CoordinatorAccessAuthorizer<Long, Long>
{
    /**
     * Constructor.
     * 
     * @param inGroupCoordMapper
     *            group coordinator mapper
     * @param inGroupMapper
     *            group mapper
     * @param inOrgMapper
     *            org mapper
     * @param inSystemAdminIdsMapper
     *            mapper to get the ids of all of the system administrators
     * @param inGetPersonIdFromAccountIdMapper
     *            mapper to get a person's id from account id
     */
    public GetAllPersonIdsWhoHaveGroupCoordinatorAccess(final DomainMapper<Long, List<Long>> inGroupCoordMapper,
            final DomainMapper<List<Long>, List<DomainGroupModelView>> inGroupMapper,
            final GetOrganizationsByShortNames inOrgMapper,
            final DomainMapper<Serializable, List<Long>> inSystemAdminIdsMapper,
            final DomainMapper<String, Long> inGetPersonIdFromAccountIdMapper)
    {
        groupCoordMapper = inGroupCoordMapper;
        groupMapper = inGroupMapper;
        orgMapper = inOrgMapper;
        systemAdminIdsMapper = inSystemAdminIdsMapper;
        getPersonIdFromAccountIdMapper = inGetPersonIdFromAccountIdMapper;
    }

    /**
     * group coordinator mapper.
     */
    private DomainMapper<Long, List<Long>> groupCoordMapper;

    /**
     * Mapper to get a person's id from account id.
     */
    private DomainMapper<String, Long> getPersonIdFromAccountIdMapper;

    /**
     * group mapper.
     */
    private DomainMapper<List<Long>, List<DomainGroupModelView>> groupMapper;

    /**
     * org mapper.
     */
    private GetOrganizationsByShortNames orgMapper;

    /**
     * mapper to get a list of all system administrators ids.
     */
    private DomainMapper<Serializable, List<Long>> systemAdminIdsMapper;

    /**
     * Gets all people Ids that have coordinator access to a group.
     * 
     * @param inGroupId
     *            the ID of the group to fetch coordinators for.
     * @return A set of Ids for the coordinators.
     */
    @SuppressWarnings("unchecked")
    public Set<Long> execute(final Long inGroupId)
    {
        // get group from mapper so we can find the parent org id.
        List<Long> groupId = new LinkedList();
        groupId.add(inGroupId);

        List<DomainGroupModelView> groupReturn = groupMapper.execute(groupId);

        // Try to get group. If not returned it is because it is an invalid or pending group
        if (groupReturn.size() != 1)
        {
            // return an empty set so callers that call contains() on it won't crash
            return new HashSet<Long>();
        }

        // get the groups coordinators from mappers.
        Set<Long> groupCoordinatorIds = new HashSet(groupCoordMapper.execute(inGroupId));

        // add in all of the system administrators
        List<Long> adminIds = systemAdminIdsMapper.execute(null);

        // add the groups together
        groupCoordinatorIds.addAll(adminIds);
        return groupCoordinatorIds;
    }

    /**
     * Determine if a person with the input id has coordinator access for the group or the parent org of the group with
     * the input id, or any of its parents up the org tree.
     * 
     * @param inGroupId
     *            the id of the group to check access to
     * @param inUserPersonId
     *            the id of the person to check access for
     * @return whether the person is an org coordinator for the org
     */
    public boolean hasGroupCoordinatorAccessRecursively(final Long inUserPersonId, final Long inGroupId)
    {
        return execute(inGroupId).contains(inUserPersonId);
    }

    /**
     * Determine if a person with the input id has coordinator access for the group or the parent org of the group with
     * the input id, or any of its parents up the org tree..
     * 
     * @param inUserPersonAccountId
     *            the account name of the person to check access to.
     * @param inGroupId
     *            the id of the group to check against.
     * @return whether the person has group coordinator access.
     */
    public boolean hasGroupCoordinatorAccessRecursively(final String inUserPersonAccountId, final Long inGroupId)
    {
        Long personId = getPersonIdFromAccountIdMapper.execute(inUserPersonAccountId);
        return hasGroupCoordinatorAccessRecursively(personId, inGroupId);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Boolean hasCoordinatorAccessRecursively(final Long inPersonId, final Long inEntityId)
    {
        return hasGroupCoordinatorAccessRecursively(inPersonId, inEntityId);
    }

}
