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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetCoordinatorIdsByGroupId;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Mapper to get a Set of IDs of all of the organizations recursively above the
 * input org id, loading from cache if possible, from DB if not, then populating
 * the cache.
 */
public class GetAllPersonIdsWhoHaveGroupCoordinatorAccess extends
        CachedDomainMapper
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
     * @param inOrgCoordinators
     *            org coordinator mapper
     * @param inPeopleMapper
     *            the person mapper to lookup people by account ID.
     * @param inCache
     *            the cache
     */
    public GetAllPersonIdsWhoHaveGroupCoordinatorAccess(
            final GetCoordinatorIdsByGroupId inGroupCoordMapper,
            final GetDomainGroupsByIds inGroupMapper,
            final GetOrganizationsByShortNames inOrgMapper,
            final GetRecursiveOrgCoordinators inOrgCoordinators,
            final GetPeopleByAccountIds inPeopleMapper,
            final Cache inCache

    )
    {
        groupCoordMapper = inGroupCoordMapper;
        groupMapper = inGroupMapper;
        orgMapper = inOrgMapper;
        orgCoordinators = inOrgCoordinators;
        peopleMapper = inPeopleMapper;
        setCache(inCache);
    }

    /**
     * group coordinator mapper.
     */
    private GetCoordinatorIdsByGroupId groupCoordMapper;

    /**
     * group coordinator mapper.
     */
    private GetPeopleByAccountIds peopleMapper;

    /**
     * group mapper.
     */
    private GetDomainGroupsByIds groupMapper;

    /**
     * org mapper.
     */
    private GetOrganizationsByShortNames orgMapper;

    /**
     * org coordinator mapper.
     */
    private GetRecursiveOrgCoordinators orgCoordinators;

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

        DomainGroupModelView thisGroup;

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

        thisGroup = (DomainGroupModelView) groupReturn.get(0);

        // get the groups coordinators from mappers.
        Set<Long> groupCoordinatorIds = new HashSet(groupCoordMapper
                .execute(inGroupId));

        // get org so we can get id.
        List<String> orgName = new LinkedList();
        orgName.add(thisGroup.getParentOrganizationShortName());
        // If you have a group then you will have a Org no need to try catch
        // this.
        OrganizationModelView pOrg = (OrganizationModelView) orgMapper.execute(
                orgName).get(0);

        // finally use the org ID to get recursive org coordinators.
        Set<Long> orgCoordinatorIds = orgCoordinators.execute(pOrg
                .getEntityId());

        // add the groups together
        groupCoordinatorIds.addAll(orgCoordinatorIds);
        return groupCoordinatorIds;
    }

    /**
     * Determine if a person with the input id has coordinator access for the
     * group or the parent org of the group with the input id, or any of its
     * parents up the org tree.
     *
     * @param inGroupId
     *            the id of the group to check access to
     * @param inUserPersonId
     *            the id of the person to check access for
     * @return whether the person is an org coordinator for the org
     */
    public boolean hasGroupCoordinatorAccessRecursively(
            final Long inUserPersonId, final Long inGroupId)
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
        List<String> person = new LinkedList<String>();
        person.add(inUserPersonAccountId);
        Long personId = peopleMapper.execute(person).get(0).getEntityId();
        return hasGroupCoordinatorAccessRecursively(personId, inGroupId);
    }

}
