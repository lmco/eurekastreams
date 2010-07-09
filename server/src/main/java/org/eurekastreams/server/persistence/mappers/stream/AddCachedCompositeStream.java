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

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Add to the cache a composite stream by the composite steam id.
 *
 */
public class AddCachedCompositeStream extends CachedDomainMapper
{    
    /**
     * Mapper to get composite steams for a given person.
     */
    private UserCompositeStreamIdsMapper userStreamsMapper;
        
    /**
     * Constuctor for the mapper.
     * 
     * @param inUserStreamsMapper
     *          The user streams mapper.
     */
    public AddCachedCompositeStream(final UserCompositeStreamIdsMapper inUserStreamsMapper)
    {
        this.userStreamsMapper = inUserStreamsMapper;
    }
    
    /**
     * Given an activity, update the cache in all places needed.
     * 
     * @param inUserId
     * 		  the Id of the user that owns the composite stream to be deleted.
     * @param inStreamView
     *            the steam view to put into the cache after database lookup.
     * @return Boolean
     * 		  successful deletion of the composite stream from the cache.
     */
    @SuppressWarnings("unchecked")
    public Boolean execute(final Long inUserId, final StreamView inStreamView)
    {
        // since the inStreamView object is non-serializable at this point, we must
        //  query to database and build a new StreamView object to put into the cache.
        StringBuilder query = new StringBuilder("FROM StreamView WHERE ");
        query.append("id=").append(inStreamView.getId());

        Query q = getEntityManager().createQuery(query.toString());

        List<StreamView> results = q.getResultList();
        StreamView resultStreamView = results.get(0);
        
        // add the StreamView object to the cache.
        getCache().set(CacheKeys.COMPOSITE_STREAM_BY_ID + inStreamView.getId(), resultStreamView);

        // get the list of composite streams for this user.
        List<Long> compositeStreamIds = userStreamsMapper.execute(inUserId);
        
        if (!compositeStreamIds.contains(inStreamView.getId()))
        {
            // add the new composite stream id to the list of composite streams for this user and set in the cache.
            compositeStreamIds.add(inStreamView.getId());        
            getCache().setList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + inUserId, compositeStreamIds);
        }


        
        return true;
    }    
    
}
