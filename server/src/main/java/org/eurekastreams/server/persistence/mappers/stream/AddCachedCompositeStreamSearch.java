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

import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Add to the cache a composite stream search object.
 *
 */
public class AddCachedCompositeStreamSearch extends CachedDomainMapper
{
    /**
     * Mapper for composite stream searches for a person.
     */
    private UserCompositeStreamSearchIdsMapper userSearchesMapper;
        
    /**
     * Constructor.
     * 
     * @param inUserSearchesMapper
     *          The user searches mapper.
     */
    public AddCachedCompositeStreamSearch(final UserCompositeStreamSearchIdsMapper inUserSearchesMapper)
    {
        this.userSearchesMapper = inUserSearchesMapper;
    }
    
    /**
     * Given an activity, update the cache in all places needed.
     * 
     * @param inUserId
     *            the user id as the owner of the composite stream search.
     * @param inStreamSearch
     *            the composite stream search object.
     * @return Boolean
     *            successful addition of the composite stream search into the cache.
     */
    @SuppressWarnings("unchecked")
    public Boolean execute(final Long inUserId, final StreamSearch inStreamSearch)
    {
        // since the inStreamSearch object is non-serializable at this point, we must
        //  query to database and build a new StreamSearch object to put into the cache.
        StringBuilder query = new StringBuilder("FROM StreamSearch WHERE ");
        query.append("id=").append(inStreamSearch.getId());

        Query q = getEntityManager().createQuery(query.toString());

        List<StreamSearch> results = q.getResultList();
        StreamSearch resultStreamSearch = results.get(0);
        
	// add the StreamView object to the cache.
	getCache().set(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + inStreamSearch.getId(), resultStreamSearch);

	// get the list of composite stream searches for this user.
	List<Long> compositeStreamSearchIds = userSearchesMapper.execute(inUserId);
	
	if (!compositeStreamSearchIds.contains(inStreamSearch.getId()))
	{
	    // add the new composite stream id to the list of composite streams for this user and set in the cache.
	    compositeStreamSearchIds.add(inStreamSearch.getId());        
	    getCache().setList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + inUserId, compositeStreamSearchIds);
	}
       
	return true;	    	    
    }    
    
}
