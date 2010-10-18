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
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Transforms a JSON request into a list of IDs that correspond to the group the person follows.
 */
public class FollowedGroupsPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Log.
     */
    Log log = LogFactory.make();

    /**
     * Mapper to get a person id by account id.
     */
    private DomainMapper<String, Long> getPersonIdByAccountId;

    /**
     * Followed groups mapper.
     */
    private DomainMapper<Long, List<Long>> followedGroupsMapper;

    /**
     * Group mapper.
     */
    private DomainMapper<List<Long>, List<DomainGroupModelView>> groupMapper;

    /**
     * Constructor.
     * 
     * @param inGetPersonIdByAccountId
     *            Mapper to get a person id by account id.
     * @param inFolloweGroupsMapper
     *            the followed groups mapper.
     * @param inGroupMapper
     *            the group mapper.
     */
    public FollowedGroupsPersistenceRequestTransformer(final DomainMapper<String, Long> inGetPersonIdByAccountId,
            final DomainMapper<Long, List<Long>> inFolloweGroupsMapper,
            final DomainMapper<List<Long>, List<DomainGroupModelView>> inGroupMapper)
    {
        getPersonIdByAccountId = inGetPersonIdByAccountId;
        followedGroupsMapper = inFolloweGroupsMapper;
        groupMapper = inGroupMapper;
    }

    /**
     * Transforms a JSON request into a list of IDs that correspond to the group the person follows.
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
        Long requestAccountId = getPersonIdByAccountId.execute(accountId);

        ArrayList<Long> results = new ArrayList<Long>();

        // If it doesn't require the current user, or the request is for the current user.
        if (userEntityId.equals(requestAccountId))
        {
            // List of group IDs.
            List<Long> groupIds = followedGroupsMapper.execute(requestAccountId);

            // List of Group Model Views.
            List<DomainGroupModelView> groupList = groupMapper.execute(groupIds);

            for (DomainGroupModelView group : groupList)
            {
                results.add(group.getStreamId());
            }

        }

        return results;
    }
}
