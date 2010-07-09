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

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Update the composite stream in the cache.
 *
 */
public class UpdateCachedCompositeStream extends CachedDomainMapper
{
    /**
     * Given a composite stream, update the cache.
     * 
     * @param inStreamViewId
     *            id of the composite stream object.
     * @return Boolean
     * 		  successful update of the composite stream in the cache.
     */
    public Boolean execute(final Long inStreamViewId)
    {
        Query q = getEntityManager().createQuery("FROM StreamView sv WHERE sv.id = :streamViewId")
        .setParameter("streamViewId", inStreamViewId);
 	
        StreamView resultStreamView = (StreamView) q.getSingleResult();
    	                
        // add the StreamView object to the cache.    	
        getCache().set(CacheKeys.COMPOSITE_STREAM_BY_ID + inStreamViewId, resultStreamView);
    	
        return true;	    
    }    
    
}
