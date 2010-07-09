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

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Update in the cache a composite stream search object.
 *
 */
public class UpdateCachedCompositeStreamSearch extends CachedDomainMapper
{        
    /**
     * Given a composite stream search, update the cache in all places needed.
     * 
     * @param inStreamSearch
     *            the composite stream search object.
     * @return Boolean
     * 				successful addition of the composite stream search into the cache.
     */
    public Boolean execute(final StreamSearch inStreamSearch)
    {
        // since the inStreamSearch object is non-serializable at this point, we must
        //  query to database and build a new StreamSearch object to put into the cache.       
        Query q = getEntityManager().createQuery("FROM StreamSearch ss WHERE ss.id = :streamSearchId")
        .setParameter("streamSearchId", inStreamSearch.getId());
    
        StreamSearch resultStreamSearch = (StreamSearch) q.getSingleResult();
        
        // add the StreamView object to the cache.      
        getCache().set(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + inStreamSearch.getId(), resultStreamSearch);
        
        return true;        
    }    
    
}
