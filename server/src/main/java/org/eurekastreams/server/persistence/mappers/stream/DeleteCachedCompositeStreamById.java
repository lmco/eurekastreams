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

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Delete from the cache the composite stream by id in the list of composite streams for a person.  
 * This class does not delete the composite stream object from its place in
 * the cache.
 *
 */
public class DeleteCachedCompositeStreamById extends CachedDomainMapper
{    
    /**
     * The mapper to obtain the composite stream id list for a person.
     */
    private UserCompositeStreamIdsMapper userStreamsMapper;
    
    /**
     * The constructor.
     * 
     * @param inUserStreamsMapper
     *          the user streams mapper.
     */
    public DeleteCachedCompositeStreamById(final UserCompositeStreamIdsMapper inUserStreamsMapper)
    {
        this.userStreamsMapper = inUserStreamsMapper;
    }    

    /**
     * Given the composite stream, remove it from the list of composite streams for a person.
     * 
     * @param inUserId
     *            the Id of the user that owns the composite stream to be deleted.
     * @param inCompositeStreamId
     *            the composite stream id to remove from the cache.
     * @return Boolean
     * 		  successful deletion of the composite stream from the cache.
     */
    public Boolean execute(final Long inUserId, final Long inCompositeStreamId)
    {
        final String compositeStreamListKey = CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + inUserId;
        
        // gets the list of composite stream ids for the given user.
        List<Long> compositeStreamIds = userStreamsMapper.execute(inUserId);
        
        // if the list from the cache is not null, remove the list item and set back into the cache
        if (compositeStreamIds != null) 
        {
        	compositeStreamIds.remove(inCompositeStreamId);
        	getCache().setList(compositeStreamListKey, compositeStreamIds);
        }
               
        return true;
    }    
    
}
