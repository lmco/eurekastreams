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
 * Delete from the cache implementation a stream view by the steam view id.
 *
 */
public class DeleteCachedCompositeStreamSearchById extends CachedDomainMapper
{
    
    /**
     * The mapper to obtain the composite stream id list for a person.
     */
    private UserCompositeStreamSearchIdsMapper userSearchesMapper;
    
    /**
     * The constructor.
     * 
     * @param inUserSearchesMapper
     *          the user searches mapper.
     */
    public DeleteCachedCompositeStreamSearchById(final UserCompositeStreamSearchIdsMapper inUserSearchesMapper)
    {
        this.userSearchesMapper = inUserSearchesMapper;
    }
    
    /**
     * Given an activity, update the cache in all places needed.
     * 
     * @param inUserId
     *                the Id of the user that owns the composite stream search being removed.
     * @param inCompositeStreamSearchId
     *            the composite stream search id to remove from the cache.
     * @return Boolean
     *            successful deletion of the composite stream from the cache.
     */
	public Boolean execute(final Long inUserId, final Long inCompositeStreamSearchId)
    {
	// delete the composite stream search given the id
        getCache().delete(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + inCompositeStreamSearchId);
        
        // gets the list of composite stream searches for the given person id
        List<Long> compositeStreamSearchIds = userSearchesMapper.execute(inUserId);
        
        // if the list from the cache is not null, remove the item and set back into the cache
        if (compositeStreamSearchIds != null) 
        {
        	compositeStreamSearchIds.remove(inCompositeStreamSearchId);
        	getCache().setList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + inUserId, 
        	        compositeStreamSearchIds);
        }
        return true;
    }    
    
}
