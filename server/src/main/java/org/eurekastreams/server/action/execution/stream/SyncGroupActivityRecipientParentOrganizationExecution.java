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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.stream.SyncGroupActivityRecipientParentOrganizationRequest;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetRecursiveParentOrgIds;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.db.GetActivityIdsPostedToStreamByUniqueKeyAndScopeType;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Sync the recipientParentOrg for all activities posted to a group in the DB and queue up task to sync a the groups up
 * in cache and search index upon successful DB update.
 *
 */
public class SyncGroupActivityRecipientParentOrganizationExecution implements
        TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Mapper to update recipient parent org id for all activites for a group.
     */
    private DomainMapper<String, Integer> syncActivityRecipientParentOrg;

    // TODO: refactor following mapper to implement interfaces.
    /**
     * Mapper to get activity ids posted to a group.
     */
    private GetActivityIdsPostedToStreamByUniqueKeyAndScopeType activityIdMapper;

    /**
     * Mapper to get Orgs by short name.
     */
    private GetOrganizationsByShortNames getOrgByShortNameMapper;

    /**
     * Mapper to get Orgs by id.
     */
    private GetOrganizationsByIds getOrgByIdMapper;

    /**
     * Mapper to get recursive org parents.
     */
    private GetRecursiveParentOrgIds getRecursiveOrgParentMapper;

    /**
     * Constructor.
     *
     * @param inSyncActivityRecipientParentOrg
     *            Mapper to update recipient parent org id for all activites for a group.
     * @param inActivityIdMapper
     *            Mapper to get activity ids posted to a group.
     * @param inGetOrgByShortNameMapper
     *            Mapper to get Orgs by short name.
     * @param inGetOrgByIdMapper
     *            Mapper to get Orgs by id.
     * @param inGetRecursiveOrgParentMapper
     *            Mapper to get recursive org parents.
     */
    public SyncGroupActivityRecipientParentOrganizationExecution(
            final DomainMapper<String, Integer> inSyncActivityRecipientParentOrg,
            final GetActivityIdsPostedToStreamByUniqueKeyAndScopeType inActivityIdMapper,
            final GetOrganizationsByShortNames inGetOrgByShortNameMapper,
            final GetOrganizationsByIds inGetOrgByIdMapper, //
            final GetRecursiveParentOrgIds inGetRecursiveOrgParentMapper)
    {
        syncActivityRecipientParentOrg = inSyncActivityRecipientParentOrg;
        activityIdMapper = inActivityIdMapper;
        getOrgByShortNameMapper = inGetOrgByShortNameMapper;
        getOrgByIdMapper = inGetOrgByIdMapper;
        getRecursiveOrgParentMapper = inGetRecursiveOrgParentMapper;
    }

    /**
     * Sync the recipientParentOrg for all activities posted to a group in the DB and queue up task to sync a the groups
     * up in cache and search index upon successful DB update.
     *
     * @param inActionContext
     *            {@link ActionContext}.
     * @return null;
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        SyncGroupActivityRecipientParentOrganizationRequest request = // \n
        (SyncGroupActivityRecipientParentOrganizationRequest) inActionContext.getActionContext().getParams();

        String groupUniqueKey = request.getGroupKey();

        // update all activities for given group in one fell swoop.
        syncActivityRecipientParentOrg.execute(groupUniqueKey);

        // get all activity ids to update
        List<Long> activityIds = activityIdMapper.execute(ScopeType.GROUP, groupUniqueKey);

        // queue up async tasks to sync the recipientParentOrg for activities in cache and search index.
        for (Long activityId : activityIds)
        {
            inActionContext.getUserActionRequests().add(new UserActionRequest("indexActivityById", null, activityId));

            inActionContext.getUserActionRequests().add(
                    new UserActionRequest("deleteCacheKeysAction", null, new HashSet<String>(Arrays
                            .asList(CacheKeys.ACTIVITY_BY_ID + activityId))));
        }

        // get recursive parent org ids for both new and old parent orgs
        List<OrganizationModelView> parentOrgs = getOrgByShortNameMapper.execute(Arrays.asList(request
                .getNewOrgParentKey(), request.getOldOrgParentKey()));

        HashSet<Long> orgIds = new HashSet<Long>();
        for (OrganizationModelView org : parentOrgs)
        {
            orgIds.add(org.getEntityId());
            orgIds.addAll(getRecursiveOrgParentMapper.execute(org.getEntityId()));
        }

        // get compositeStream ids for all affected orgs
        // List<OrganizationModelView> allOrgs = getOrgByIdMapper.execute(new ArrayList<Long>(orgIds));
        // for (OrganizationModelView org : allOrgs)
        // {
        // inActionContext.getUserActionRequests().add(
        // new UserActionRequest("deleteCacheKeysAction", null, new HashSet<String>(Arrays
        // .asList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + org.getCompositeStreamId()))));
        //
        // }

        return null;
    }
}
