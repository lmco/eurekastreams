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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamView;

/**
 * Gets a list of all activity ids for a given composite stream.
 */
public class CompositeStreamActivityIdsMapper
{     
    /**
     * The compositeStreamMapper.
     */
    private BulkCompositeStreamsMapper compositeStreamMapper;
    
    /**
     * Collection of loaders for CompositeStream types.
     */
    private Map<StreamView.Type, CompositeStreamLoader> compositeStreamLoaders;
    
    /**
     * Constructor.
     * @param inGetCompositeStreamsByIdDAO DAO for getting CompositeStream by id.
     */
    public CompositeStreamActivityIdsMapper(final BulkCompositeStreamsMapper inGetCompositeStreamsByIdDAO)
    {
        compositeStreamMapper = inGetCompositeStreamsByIdDAO;
    }

    /**
     * Returns list of ActivityIds for provided user/compositeStreamId.
     * 
     * @param inCompositeStreamId
     *            the composite stream of interest.
     * @param inUserId
     *            The user id.
     * @return The list of ActivityIds for provided user/compositeStreamId.
     */
    public List<Long> execute(final long inCompositeStreamId, final long inUserId)
    {       
        //get appropriate loader implementation based on compositeStream type.
        StreamFilter compositeStream = getCompositeStreamById(inCompositeStreamId);
        CompositeStreamLoader loader = getCompositeStreamLoader((StreamView) compositeStream);
        
        //execute the loader and return the results.
        return loader.getActivityIds((StreamView) compositeStream, inUserId);                                       
    }
    
    /**
     * Get CompositeStream by id.
     * @param inCompositeStreamId The id.
     * @return The CompositeStream with given id.
     */
    private StreamFilter getCompositeStreamById(final long inCompositeStreamId)    
    {
        List<Long> ids = new ArrayList<Long>(1);
        ids.add(inCompositeStreamId);
        List<StreamFilter> result = compositeStreamMapper.execute(ids);
        if (result.size() != 1)
        {
            throw new RuntimeException("Error looking up compositeStream with id: " + inCompositeStreamId);
        }
        return result.get(0);        
    }
    
    /**
     * Gets CompositeStreamLoader for a given CompositeStream.
     * @param inCompositeStream The CompositeStream
     * @return The CompositeStreamLoader.
     */
    private CompositeStreamLoader getCompositeStreamLoader(final StreamView inCompositeStream)
    {
        StreamView.Type type = (inCompositeStream.getType() == null) 
            ? StreamView.Type.NOTSET 
            : inCompositeStream.getType();
    
        CompositeStreamLoader loader = compositeStreamLoaders.get(type);
    
        if (loader == null)
        {
            throw new RuntimeException("No loader for compositeStream of type: " + inCompositeStream.getId());
        }
        
        return loader;
    }

    /**
     * @return the compositeStreamLoaders
     */
    public Map<StreamView.Type, CompositeStreamLoader> getCompositeStreamLoaders()
    {
        return compositeStreamLoaders;
    }

    /**
     * @param inCompositeStreamLoaders the compositeStreamLoaders to set
     */
    public void setCompositeStreamLoaders(final Map<StreamView.Type, CompositeStreamLoader> inCompositeStreamLoaders)
    {
        this.compositeStreamLoaders = inCompositeStreamLoaders;
    }

    /**
     * @return the compositeStreamMapper
     */
    public BulkCompositeStreamsMapper getCompositeStreamMapper()
    {
        return compositeStreamMapper;
    }

    /**
     * @param inCompositeStreamMapper the compositeStreamMapper to set
     */
    public void setCompositeStreamMapper(final BulkCompositeStreamsMapper inCompositeStreamMapper)
    {
        this.compositeStreamMapper = inCompositeStreamMapper;
    }       
}
