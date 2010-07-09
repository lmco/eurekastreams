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
package org.eurekastreams.server.service.actions.response;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamFilter;

/**
 * Response object for stream filter actions.
 *
 */
public class GetCurrentUserStreamFiltersResponse implements Serializable
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 883455100110011177L;

    /**
     * List of stream views.
     */
    private List<StreamFilter> streamFilters;
    
    /**
     * Hidden line index for user's stream filters.
     */
    private Integer hiddenLineIndex;
    
    /**
     * Constructor.
     */
    public GetCurrentUserStreamFiltersResponse()
    {
        //default constructor for serialization.
    }

    /**
     * Constructor.
     * @param inHiddenLineIndex The hidden line index.
     * @param inStreamFilters List of user's StreamFilters
     */
    public GetCurrentUserStreamFiltersResponse(final Integer inHiddenLineIndex, 
            final List<StreamFilter> inStreamFilters)
    {
        hiddenLineIndex = inHiddenLineIndex;
        streamFilters = inStreamFilters;
    }

    /**
     * @return the StreamFilter
     */
    public List<StreamFilter> getStreamFilters()
    {
        return streamFilters;
    }
    
    /**
     * @param inStreamFilters the streamViews to set
     */
    public void setStreamFilters(final List<StreamFilter> inStreamFilters)
    {
        this.streamFilters = inStreamFilters;
    }

    /**
     * @return the hiddenLineIndex
     */
    public Integer getHiddenLineIndex()
    {
        return hiddenLineIndex;
    }

    /**
     * @param inHiddenLineIndex the hiddenLineIndex to set
     */
    public void setHiddenLineIndex(final Integer inHiddenLineIndex)
    {
        this.hiddenLineIndex = inHiddenLineIndex;
    }

}
