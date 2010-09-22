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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.DeleteFromSearchIndexRequest;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.MoveOrganizationPeopleRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Delete an organization. This assumes that the organization has no orgs or groups underneath it. People reporting to
 * org will be moved to deleted org's parent org, org will be removed from people's related orgs collection.
 *
 */
public class DeleteOrganizationExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    // TODO: Change mapppers over to interfaces when mappers have been refactored.
    /**
     * Mapper to move People out of organization.
     */
    private DomainMapper<MoveOrganizationPeopleRequest, Set<Long>> movePeopleMapper;

    /**
     * Mapper to get person ids for those that have given org as related org.
     */
    private DomainMapper<Long, Set<Long>> relatedOrgPersonIdsMapper;

    /**
     * Mapper for getting organization DTOs.
     */

    private GetOrganizationsByIds orgDTOByIdMapper;

    /**
     * {@link FindByIdMapper}.
     */
    private FindByIdMapper<Organization> orgByIdMapper;

    /**
     * Mapper to delete org and related objects.
     */
    private DomainMapper<Long, Boolean> deleteOrgMapper;

    /**
     * {@link OrganizationMapper}. This is used for updating org stats only.
     */
    @SuppressWarnings("deprecation")
    private OrganizationMapper organizationMapper;

    /**
     * The organization hierarchy traverser builder - needed because this class is reused by all threads, we can't share
     * OrganizationHierarchyTraversers.
     */
    private OrganizationHierarchyTraverserBuilder orgTraverserBuilder;

    /**
     * Constructor.
     *
     * @param inMovePeopleMapper
     *            Mapper to move People out of organization.
     * @param inOrgDTOByIdMapper
     *            Mapper for getting organization DTOs.
     * @param inOrgByIdMapper
     *            {@link FindByIdMapper}.
     * @param inDeleteOrgMapper
     *            Mapper to delete org and related objects.
     * @param inRelatedOrgPersonIdsMapper
     *            Mapper to get person ids for all users that have deleted org as related org.
     * @param inOrganizationMapper
     *            {@link OrganizationMapper}. This is used for updating org stats only.
     * @param inOrgTraverserBuilder
     *            The organization hierarchy traverser builder.
     */
    public DeleteOrganizationExecution(final DomainMapper<MoveOrganizationPeopleRequest, Set<Long>> inMovePeopleMapper,
            final GetOrganizationsByIds inOrgDTOByIdMapper, final FindByIdMapper<Organization> inOrgByIdMapper,
            final DomainMapper<Long, Boolean> inDeleteOrgMapper,
            final DomainMapper<Long, Set<Long>> inRelatedOrgPersonIdsMapper,
            final OrganizationMapper inOrganizationMapper,
            final OrganizationHierarchyTraverserBuilder inOrgTraverserBuilder)
    {
        movePeopleMapper = inMovePeopleMapper;
        orgDTOByIdMapper = inOrgDTOByIdMapper;
        orgByIdMapper = inOrgByIdMapper;
        deleteOrgMapper = inDeleteOrgMapper;
        relatedOrgPersonIdsMapper = inRelatedOrgPersonIdsMapper;
        organizationMapper = inOrganizationMapper;
        orgTraverserBuilder = inOrgTraverserBuilder;
    }

    /**
     * Delete Organization and directly associated entities.
     *
     * @param inActionContext
     *            The action context.
     * @return parent org short name;
     */
    @SuppressWarnings("deprecation")
    @Override
    public String execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        // ***START DB UPDATES****

        // get org id to delete.
        Long orgId = (Long) inActionContext.getActionContext().getParams();

        // get org DTO and parent org entity (entity needed later)..
        OrganizationModelView orgDto = orgDTOByIdMapper.execute(orgId);
        Organization parentOrg = orgByIdMapper.execute(new FindByIdRequest("Organization", orgDto
                .getParentOrganizationId()));

        // move all people out of org.
        Set<Long> movedPeopleIds = movePeopleMapper.execute(new MoveOrganizationPeopleRequest(orgDto.getShortName(),
                parentOrg.getShortName()));

        // get ids of people that have org as related org.
        Set<Long> relatedOrgPersonIds = relatedOrgPersonIdsMapper.execute(orgId);

        // delete the org.
        deleteOrgMapper.execute(orgId);

        // recalculate org statistics for branch where org removed.
        OrganizationHierarchyTraverser orgTraverser = orgTraverserBuilder.getOrganizationHierarchyTraverser();
        orgTraverser.traverseHierarchy(parentOrg);
        organizationMapper.updateOrganizationStatistics(orgTraverser);

        // ***END DB UPDATES****

        // queue up async tasks.

        // Only reindex all people that were moved out of org.
        HashSet<String> cacheKeysToRemove = new HashSet<String>();
        for (Long personId : movedPeopleIds)
        {
            inActionContext.getUserActionRequests().add(new UserActionRequest("indexPersonById", null, personId));
        }

        // re-cache both moved people and people that had org as related org.
        Set<Long> personCacheKeysToModify = new HashSet<Long>(movedPeopleIds);
        personCacheKeysToModify.addAll(relatedOrgPersonIds);
        for (Long personId : personCacheKeysToModify)
        {
            cacheKeysToRemove.add(CacheKeys.PERSON_BY_ID + personId);
        }

        // reindex and re-cache all orgs up tree from deleted org
        // already have parent orgs from updating stats, so use them again here to avoid more calls to datastores.
        Set<Organization> parentOrgs = orgTraverser.getOrganizations();
        for (Organization org : parentOrgs)
        {
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest("indexOrganizationById", null, org.getId()));
            cacheKeysToRemove.add(CacheKeys.ORGANIZATION_BY_ID + org.getId());
            cacheKeysToRemove.add(CacheKeys.ORGANIZATION_BY_SHORT_NAME + org.getShortName());
            cacheKeysToRemove.add(CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + org.getId());
        }

        // remove cache keys for org being deleted.
        cacheKeysToRemove.add(CacheKeys.ORGANIZATION_BY_ID + orgId);
        cacheKeysToRemove.add(CacheKeys.ORGANIZATION_BY_SHORT_NAME + orgDto.getShortName());
        cacheKeysToRemove.add(CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + orgId);

        // remove org from search index
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteFromSearchIndexAction", null, new DeleteFromSearchIndexRequest(
                        Organization.class, Arrays.asList(orgId))));

        // remove org tree DTO
        cacheKeysToRemove.add(CacheKeys.ORGANIZATION_TREE_DTO);

        // remove all major components from cache. This is not all inclusive as the peripheral stuff will just
        // expire away and there's no real need to keep maintaining this list as cache is modified.
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteCacheKeysAction", null, cacheKeysToRemove));

        return parentOrg.getShortName();
    }
}
