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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Adds data to the cache for a newly created activity.
 */
public class PostCachedActivity extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to get followers of a person.
     */
    private final DomainMapper<Long, List<Long>> personFollowersMapper;

    /**
     * Mapper to get personmodelview from an accountid.
     */
    private final DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * Constructor.
     * 
     * @param inPersonFollowersMapper
     *            person follower mapper.
     * @param inGetPersonModelViewByAccountIdMapper
     *            Mapper to get personmodelview from an accountid.
     */
    public PostCachedActivity(final DomainMapper<Long, List<Long>> inPersonFollowersMapper,
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper)
    {
        personFollowersMapper = inPersonFollowersMapper;
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
    }

    /**
     * Adds a new item in cache for the activity itself and adds the id to all necessary composite stream activity id
     * lists.
     * 
     * @param activity
     *            the activity to be added.
     */
    public void execute(final Activity activity)
    {
        ScopeType type = activity.getRecipientStreamScope().getScopeType();
        String recipientUniqueKey = activity.getRecipientStreamScope().getUniqueKey();
        long activityId = activity.getId();

        if (type == ScopeType.PERSON)
        {
            PersonModelView person = getPersonModelViewByAccountIdMapper.execute(recipientUniqueKey);

            updateActivitiesByFollowingCacheLists(person.getEntityId(), activityId);

        }
        else if (type == ScopeType.RESOURCE && activity.getActorType() == EntityType.PERSON)
        {
            if (activity.getShowInStream())
            {
                PersonModelView person = getPersonModelViewByAccountIdMapper.execute(activity.getActorId());
                updateActivitiesByFollowingCacheLists(person.getEntityId(), activityId);
            }
        }
        else if (type == ScopeType.GROUP)
        {
            // no-op
            int donothing = 1;
        }
        else
        {
            throw new RuntimeException("Unexpected Activity destination stream type: " + type);
        }

        if (activity.getSharedLink() != null)
        {
            // this has a shared link - add this activity id to the top of the shared resource's stream in cache
            log.info("Adding activity with a shared resource (id:" + activity.getSharedLink().getStreamScope().getId()
                    + ") to the top of the shared resource's cached activity stream.");
            getCache()
                    .addToTopOfList(
                            CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + activity.getSharedLink().getStreamScope().getId(),
                            activityId);
        }

        if (activity.getShowInStream())
        {
            // add to everyone list
            log.trace("Adding activity id " + activityId + " into everyone activity list.");
            getCache().addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, activityId);
        }
    }

    /**
     * Update the Activities by following cache lists when a stream.
     * 
     * @param inPersonId
     *            person id.
     * @param inActivityId
     *            activity id.
     */
    private void updateActivitiesByFollowingCacheLists(final long inPersonId, final long inActivityId)
    {
        List<Long> followers = personFollowersMapper.execute(inPersonId);

        for (Long follower : followers)
        {
            getCache().addToTopOfList(CacheKeys.ACTIVITIES_BY_FOLLOWING + follower, inActivityId);
        }
    }
}
