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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.GroupStreamDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;

/**
 * This execution strategy retrieves information about all groups that a user is following.
 */
public class GetCurrentUserFollowedGroupsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to get the ids of all groups followed by this user.
     */
    private DomainMapper<Long, List<Long>> idsMapper;

    /**
     * Mapper to get populated Domain Group objects given the group's id.
     */
    private DomainMapper<List<Long>, List<DomainGroupModelView>> groupsMapper;

    /**
     * Mapper to get people.
     */
    private DomainMapper<List<Long>, List<PersonModelView>>peopleMapper;

    /**
     * Mapper to determine if a user has group coordinator access.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupPermissionsChecker;

    /**
     * Constructor.
     * 
     * @param inIdsMapper
     *            ids mapper to set.
     * @param inGroupsMapper
     *            groups mapper to set.
     * @param inPeopleMapper
     *            people mapper to set.
     * @param inGroupPermissionChecker
     *            Group permission checker.
     */
    public GetCurrentUserFollowedGroupsExecution(final DomainMapper<Long, List<Long>> inIdsMapper,
            final DomainMapper<List<Long>, List<DomainGroupModelView>> inGroupsMapper,
            final DomainMapper<List<Long>, List<PersonModelView>>inPeopleMapper,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupPermissionChecker)
    {
        idsMapper = inIdsMapper;
        groupsMapper = inGroupsMapper;
        peopleMapper = inPeopleMapper;
        groupPermissionsChecker = inGroupPermissionChecker;
    }

    /**
     * {@inheritDoc} This method uses all of the injected mappers to assemble a GetCurrentUserStreamFiltersResponse
     * object that contains the fields necessary to display the group stream details for all groups that a user follows.
     * The data is mostly contained in a GroupStreamDTO.
     */
    @Override
    public GetCurrentUserStreamFiltersResponse execute(final PrincipalActionContext inActionContext)
            throws ExecutionException
    {
        // get the user's Id
        final long userEntityId = inActionContext.getPrincipal().getId();

        List<Long> followedGroupIds = idsMapper.execute(userEntityId);

        List<DomainGroupModelView> followedGroups = groupsMapper.execute(followedGroupIds);

        List<StreamFilter> groupStreams = new ArrayList<StreamFilter>();
        for (DomainGroupModelView group : followedGroups)
        {
            boolean isStreamPostable = group.isStreamPostable();

            // if stream is marked as not postable, check if user has group coordinator access and
            // modify isStreamPostable accordingly
            isStreamPostable = isStreamPostable ? isStreamPostable : groupPermissionsChecker
                    .hasGroupCoordinatorAccessRecursively(userEntityId, group.getEntityId());

            groupStreams.add(new GroupStreamDTO(group.getEntityId(), group.getName(), group.getShortName(),
                    isStreamPostable));

            if (log.isInfoEnabled())
            {
                log.info("created groupstreamDTO: " + group.getEntityId() + "; " + group.getName() + "; "
                        + group.getShortName());
            }
        }

        List<PersonModelView> people = peopleMapper.execute(Arrays.asList(userEntityId));

        // Returns the list of group streams and related info.
        GetCurrentUserStreamFiltersResponse response = new GetCurrentUserStreamFiltersResponse(people.get(0)
                .getGroupStreamHiddenLineIndex(), groupStreams);

        return response;
    }
}
