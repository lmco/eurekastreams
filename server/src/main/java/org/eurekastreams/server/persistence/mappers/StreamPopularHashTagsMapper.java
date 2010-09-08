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
package org.eurekastreams.server.persistence.mappers;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.persistence.mappers.stream.StreamPopularHashTagsReport;

/**
 * Wrapping mapper to get the popular hashtags from another mapper, then return null if it's expired.
 */
public class StreamPopularHashTagsMapper implements
        DomainMapper<StreamPopularHashTagsRequest, StreamPopularHashTagsReport>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Number of minutes to keep the popular hashtags in cache.
     */
    private final Integer popularHashTagsExpirationInMinutes;

    /**
     * Mapper to get the popular hashtags for a stream.
     */
    private final DomainMapper<StreamPopularHashTagsRequest, StreamPopularHashTagsReport> popularHashTagsMapper;

    /**
     * Constructor.
     *
     * @param inPopularHashTagsMapper
     *            the mapper to get the popular hashtags for a stream
     * @param inPopularHashTagsExpirationInMinutes
     *            the number of minutes to allow popular hashtags in cache
     */
    public StreamPopularHashTagsMapper(
            final DomainMapper<StreamPopularHashTagsRequest, StreamPopularHashTagsReport> inPopularHashTagsMapper,
            final Integer inPopularHashTagsExpirationInMinutes)
    {
        popularHashTagsMapper = inPopularHashTagsMapper;
        popularHashTagsExpirationInMinutes = inPopularHashTagsExpirationInMinutes;
    }

    /**
     * Get the popular hashtags from the decorated mapper, returning null if it's not found or if found and too old.
     *
     * @param inRequest
     *            request containing the stream type and unique key
     * @return null if not found or found and generated too long ago
     */
    @Override
    public StreamPopularHashTagsReport execute(final StreamPopularHashTagsRequest inRequest)
    {
        StreamPopularHashTagsReport hashTagReport = popularHashTagsMapper.execute(inRequest);
        if (hashTagReport != null)
        {
            // found the report - see if it's too old
            Calendar earliestValidReportDate = Calendar.getInstance();
            earliestValidReportDate.add(Calendar.MINUTE, 0 - popularHashTagsExpirationInMinutes);

            if (hashTagReport.getReportGenerationDate().before(earliestValidReportDate.getTime()))
            {
                // this report is expired - regenerate
                if (log.isInfoEnabled())
                {
                    log.info("Popular HashTag report for stream " + inRequest.getStreamEntityScopeType() + " #"
                            + inRequest.getStreamEntityUniqueKey() + " is expired - regenerating.");
                }
                return null;
            }
        }
        return hashTagReport;
    }
}
