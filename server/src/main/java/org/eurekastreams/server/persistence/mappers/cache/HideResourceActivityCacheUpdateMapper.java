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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Mapper to update cache lists when user "deletes" hides a resource activity.
 * 
 */
public class HideResourceActivityCacheUpdateMapper extends BaseArgCachedDomainMapper<Long, Void>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * ActivityDTO DAO.
     */
    private DomainMapper<Long, ActivityDTO> activityDAO;

    /**
     * Mapper to get a PersonModelView by account id.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * DAO to get followers of a person.
     */
    private DomainMapper<Long, List<Long>> userIdsFollowingPersonDAO;

    /**
     * Constructor.
     * 
     * @param inActivityDAO
     *            ActivityDTO DAO.
     * @param inGetPersonModelViewByAccountIdMapper
     *            Mapper to get a PersonModelView by account id.
     * @param inUserIdsFollowingPersonDAO
     *            DAO to get followers of a person.
     */
    public HideResourceActivityCacheUpdateMapper(final DomainMapper<Long, ActivityDTO> inActivityDAO,
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper,
            final DomainMapper<Long, List<Long>> inUserIdsFollowingPersonDAO)
    {
        activityDAO = inActivityDAO;
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
        userIdsFollowingPersonDAO = inUserIdsFollowingPersonDAO;
    }

    /**
     * Update cache lists when user "deletes" hides a resource activity.
     * 
     * @param inRequest
     *            activity id.
     * @return void.
     */
    @Override
    public Void execute(final Long inRequest)
    {
        ActivityDTO activity = activityDAO.execute(inRequest);

        // short-circut if activity already deleted, or showInStream has been toggled back to true already or not a
        // resource type activity.
        if (activity == null || activity.getShowInStream()
                || activity.getDestinationStream().getType() != EntityType.RESOURCE)
        {
            return null;
        }

        PersonModelView actor = getPersonModelViewByAccountIdMapper.execute(activity.getActor().getUniqueIdentifier());

        // remove from actor's personal stream.
        getCache().removeFromList(CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + actor.getStreamId(), inRequest);

        // remove activity id user's "following" lists for everyone following destination stream.
        List<Long> followingUserIds = userIdsFollowingPersonDAO.execute(actor.getId());
        for (Long followerId : followingUserIds)
        {
            log.info("Removing activity #" + inRequest + " from followed list for person #" + followerId);
            getCache().removeFromList(CacheKeys.ACTIVITIES_BY_FOLLOWING + followerId, inRequest);
        }

        return null;
    }

}
