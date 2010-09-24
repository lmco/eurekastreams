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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowedGroupIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Transforms a followedGroups request.
 */
public class FollowedGroupsPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Log.
     */
    Log log = LogFactory.make();

    /**
     * Person mapper.
     */
    private GetPeopleByAccountIds personMapper;

    /**
     * Followed groups mapper.
     */
    private GetFollowedGroupIds followeGroupsMapper;

    /**
     * Group mapper.
     */
    private GetDomainGroupsByIds groupMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            the person mapper.
     * @param inFolloweGroupsMapper
     *            the followed groups mapper.
     * @param inGroupMapper
     *            the group mapper.
     */
    public FollowedGroupsPersistenceRequestTransformer(final GetPeopleByAccountIds inPersonMapper,
            final GetFollowedGroupIds inFolloweGroupsMapper, final GetDomainGroupsByIds inGroupMapper)
    {
        personMapper = inPersonMapper;
        followeGroupsMapper = inFolloweGroupsMapper;
        groupMapper = inGroupMapper;
    }

    /**
     * Transformation.
     * 
     * @param request
     *            the request.
     * @param userEntityId
     *            the user.
     * @return the new request.
     */
    @Override
    public Serializable transform(final JSONObject request, final Long userEntityId)
    {
        String accountId = request.getString("joinedGroups");

        // Request user.
        Long requestAccountId = personMapper.fetchId(accountId);

        // If it doesn't require the current user, or the request is for the current user.
        if (userEntityId.equals(requestAccountId))
        {
            // List of group IDs.
            List<Long> groupIds = followeGroupsMapper.execute(requestAccountId);

            // List of Group Model Views.
            List<DomainGroupModelView> groupList = groupMapper.execute(groupIds);

            ArrayList<Long> results = new ArrayList<Long>();

            for (DomainGroupModelView group : groupList)
            {
                results.add(group.getCompositeStreamId());
            }

            return results;
        }
        else
        {
            if (log.isDebugEnabled())
            {
                log.debug("User was: " + userEntityId + " Request from: " + requestAccountId);
            }

            throw new AuthorizationException("Insufficent priveledges to access stream.");
        }

    }

}
