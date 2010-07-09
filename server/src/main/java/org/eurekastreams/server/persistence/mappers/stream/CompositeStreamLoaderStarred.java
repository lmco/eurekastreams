/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import org.apache.commons.lang.NotImplementedException;
import org.eurekastreams.server.domain.stream.StreamView;

/**
 * CompositeStreamLoader implementation for "Starred" CompositeStream type.
 *
 */
public class CompositeStreamLoaderStarred extends BaseCompositeStreamLoader
{
    /**
     * DAO for getting starred Activity ids for a user.
     */
    private GetStarredActivityIds starredActivityIdDAO;
    
    /**
     * Constructor.
     * @param inStarredActivityIdDAO DAO for getting starred Activity ids for a user.
     */
    public CompositeStreamLoaderStarred(final GetStarredActivityIds inStarredActivityIdDAO)
    {
        starredActivityIdDAO = inStarredActivityIdDAO;
    }

    /**
     * Throws NotImplementedException. Should never be called.
     * @param inCompositeStream the CompositeStream.
     * @param inUserId the user.
     * 
     * @return restrictions hashtable to be used in returning activityId list from datastore.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Hashtable<RestrictionType, HashSet> getActivityRestrictions(final StreamView inCompositeStream, 
            final long inUserId)
    {
        throw new NotImplementedException("Starred Ids should be accessed via GetStarredActivityIds object");
    }

    /**
     * Get list of activity ids for given compositeStream and user from cache, if present, or null if not.
     * @param compositeStream The CompositeStream.
     * @param inUserId The user id.
     * @return List of activity ids for given compositeStream and user from cache, if present, or null if not.
     */
    @Override
    protected List<Long> getIdListFromCache(final StreamView compositeStream, final long inUserId)
    {
        return starredActivityIdDAO.execute(inUserId);
    }

    /**
     * Throws NotImplementedException. Should never be called.
     * @param inActivityIds The list of activity ids.
     * @param inCompositeStream The CompositeStream.
     * @param inUserId The user id.
     */
    @Override
    protected void setIdListToCache(final List<Long> inActivityIds, 
            final StreamView inCompositeStream, final long inUserId)
    {
        throw new NotImplementedException("Starred Ids should be accessed via GetStarredActivityIds object");        
    }

}
