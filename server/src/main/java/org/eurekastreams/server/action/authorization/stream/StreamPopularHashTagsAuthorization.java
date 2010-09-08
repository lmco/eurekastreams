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
package org.eurekastreams.server.action.authorization.stream;

import java.util.Set;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Authorizor for a stream's popular hashtags.
 */
public class StreamPopularHashTagsAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get domain groups by short name.
     */
    private GetDomainGroupsByShortNames groupsMapper;

    /**
     * Mapper to check for group access.
     */
    private GetPrivateCoordinatedAndFollowedGroupIdsForUser groupAccessMapper;

    /**
     * Constructor.
     *
     * @param inGroupsMapper
     *            mapper to get domain groups by short name
     * @param inGroupAccessMapper
     *            mapper to check for group access
     */
    public StreamPopularHashTagsAuthorization(final GetDomainGroupsByShortNames inGroupsMapper,
            final GetPrivateCoordinatedAndFollowedGroupIdsForUser inGroupAccessMapper)
    {
        groupsMapper = inGroupsMapper;
        groupAccessMapper = inGroupAccessMapper;
    }

    /**
     * Authorize the request for a stream's popular tags.
     *
     * @param inActionContext
     *            the action context containing the StreamPopularHashTagsRequest request
     * @throws AuthorizationException
     *             when the stream represents a private group that the user doesn't have access to
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        StreamPopularHashTagsRequest request = (StreamPopularHashTagsRequest) inActionContext.getParams();
        if (request.getStreamEntityScopeType() == ScopeType.GROUP)
        {
            // only private groups require authorization
            DomainGroupModelView group = groupsMapper.fetchUniqueResult(request.getStreamEntityUniqueKey());
            if (!group.isPublic())
            {
                Set<Long> groupsUserCanAccess = groupAccessMapper.execute(inActionContext.getPrincipal().getId());
                if (!groupsUserCanAccess.contains(group.getId()))
                {
                    throw new AuthorizationException(inActionContext.getPrincipal().getAccountId()
                            + " cannot access popular hashtags for group with short name " + group.getShortName());
                }
            }
        }
    }
}
