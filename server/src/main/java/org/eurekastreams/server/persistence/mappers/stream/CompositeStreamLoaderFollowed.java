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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Returns activity ids for "followed" CompositeStream.
 *
 */
public class CompositeStreamLoaderFollowed extends BaseCompositeStreamLoader
{
    /**
     * DAO for looking up a person by id.
     */
    private GetPeopleByIds personDAO;
    
    /**
     * DAO for looking up a group by id.
     */
    private GetDomainGroupsByIds groupDAO;
    
    /**
     * DAO for getting group ids a person is following.
     */
    private GetFollowedGroupIds followedGroupDAO;
    
    /**
     * DAO for getting person ids that a person is following.
     */
    private GetFollowedPersonIds followedPersonDAO;
    
    /**
     * Constructor.
     * @param inPersonDAO DAO for looking up person by account id.
     * @param inGroupDAO DAO for looking up group by short name.
     * @param inFollowedPersonDAO DAO for getting person ids that a person is following.
     * @param inFollowedGroupDAO  DAO for getting group ids a person is following.
     */
    public CompositeStreamLoaderFollowed(final GetPeopleByIds inPersonDAO, 
            final GetDomainGroupsByIds inGroupDAO,
            final GetFollowedPersonIds inFollowedPersonDAO,
            final GetFollowedGroupIds inFollowedGroupDAO)
    {
        personDAO = inPersonDAO;
        groupDAO = inGroupDAO;
        followedGroupDAO = inFollowedGroupDAO;
        followedPersonDAO = inFollowedPersonDAO;
    }
        
    /**
     * Returns restrictions hashtable to be used in returning activityId list from datastore.
     * @param compositeStream the CompositeStream.
     * @param inUserId the user.
     * 
     * @return restrictions hashtable to be used in returning activityId list from datastore.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Hashtable<RestrictionType, HashSet> getActivityRestrictions(final StreamView compositeStream, 
            final long inUserId)
    {               
        //get lists of people and groups a person is following.
        List<PersonModelView> people = personDAO.execute(followedPersonDAO.execute(inUserId));
        
        //create list of streamIds that represent those people and groups.
        HashSet<Long> streamIds = new HashSet<Long>();
        for (PersonModelView pmv : people)
        {
            streamIds.add(pmv.getStreamId());
        }
        
        //Set the stream ids in the results hashtable and return.
        Hashtable<RestrictionType, HashSet> results = new Hashtable<RestrictionType, HashSet>();
        if (streamIds.size() != 0)
        {
            results.put(RestrictionType.STREAM_IDS, streamIds);
        }

        return results;
    }

    /**
     * Get list of activity ids for given compositeStream and user from cache, if present, or null if not.
     * 
     * @param compositeStream
     *            The CompositeStream.
     * @param inUserId
     *            The user id.
     * @return List of activity ids for given compositeStream and user from cache, if present, or null if not.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<Long> getIdListFromCache(final StreamView compositeStream, final long inUserId)
    {
        return getCache().getList(CacheKeys.ACTIVITIES_BY_FOLLOWING + inUserId);
    }

    /**
     * Sets the list of activity ids to cache for given CompositeStream and user.
     * 
     * @param inActivityIds
     *            The list of activity ids.
     * @param inCompositeStream
     *            The CompositeStream.
     * @param inUserId
     *            The user id.
     */
    @Override
    protected void setIdListToCache(final List<Long> inActivityIds, 
            final StreamView inCompositeStream, final long inUserId)
    {
        getCache().setList(CacheKeys.ACTIVITIES_BY_FOLLOWING + inUserId, inActivityIds);        
    }    
}
