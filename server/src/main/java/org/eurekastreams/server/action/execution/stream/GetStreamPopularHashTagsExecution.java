/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.StreamPopularHashTagsReportDTO;

/**
 * Execution to get the list of popular HashTags for an activity stream.
 */
public class GetStreamPopularHashTagsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get the popular hashtags for a stream.
     */
    private final DomainMapper<StreamPopularHashTagsRequest, StreamPopularHashTagsReportDTO> popularHashTagsMapper;

    /**
     * Logger.
     */
    private Log log = LogFactory.make();
    
    /**
     * Constructor.
     *
     * @param inPopularHashTagsMapper
     *            the mapper to get the popular hashtags for a stream
     */
    public GetStreamPopularHashTagsExecution(
            final DomainMapper<StreamPopularHashTagsRequest, StreamPopularHashTagsReportDTO> inPopularHashTagsMapper)
    {
        popularHashTagsMapper = inPopularHashTagsMapper;
    }

    /**
     * Get the popular hashtags for an activity stream.
     *
     * @param inActionContext
     *            the action context
     * @return an ArrayList of the popular hashtags
     * @throws ExecutionException
     *             on error
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        log.debug("Entering");
        StreamPopularHashTagsRequest request = (StreamPopularHashTagsRequest) inActionContext.getParams();
        StreamPopularHashTagsReportDTO response = popularHashTagsMapper.execute(request);

        List<String> hashTags = response.getPopularHashTags();
        
        ArrayList<String> result = new ArrayList<String>();
        
        if (hashTags != null)
        {
            log.debug("Found " + hashTags.size() + " Popular Hashtags");
            result.addAll(hashTags);
        }
        
        return result;
    }
}
