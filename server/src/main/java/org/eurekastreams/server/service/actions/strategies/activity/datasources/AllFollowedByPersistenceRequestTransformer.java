/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
import org.eurekastreams.server.persistence.mappers.requests.GetAllFollowedByActivityIdsRequest;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 *Transforms a JSON request into GetAllFollowedByActivityIdsRequest.
 * 
 */
public class AllFollowedByPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Log.
     */
    private Log log = LogFactory.make();

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
    public AllFollowedByPersistenceRequestTransformer(final DomainMapper<String, Long> inGetPersonIdByAccountId,
            final DomainMapper<Long, List<Long>> inFolloweGroupsMapper,
            final DomainMapper<List<Long>, List<DomainGroupModelView>> inGroupMapper)
    {
        getPersonIdByAccountId = inGetPersonIdByAccountId;
        followedGroupsMapper = inFolloweGroupsMapper;
        groupMapper = inGroupMapper;
    }

    @Override
    public Serializable transform(final JSONObject inRequest, final Long inUserEntityId)
    {
        String accountId = inRequest.getString("followedBy");

        // Request user.
        Long requestUserId = getPersonIdByAccountId.execute(accountId);

        ArrayList<Long> followedGroupStreamIds = new ArrayList<Long>();

        // If it doesn't require the current user, or the request is for the current user.
        if (inUserEntityId.equals(requestUserId))
        {
            // List of group IDs.
            List<Long> groupIds = followedGroupsMapper.execute(requestUserId);

            // List of Group Model Views.
            List<DomainGroupModelView> groupList = groupMapper.execute(groupIds);

            for (DomainGroupModelView group : groupList)
            {
                followedGroupStreamIds.add(group.getStreamId());
            }
        }

        return new GetAllFollowedByActivityIdsRequest(requestUserId, followedGroupStreamIds);
    }

}
