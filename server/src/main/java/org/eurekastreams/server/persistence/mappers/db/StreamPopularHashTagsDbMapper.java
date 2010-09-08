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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.StreamPopularHashTagsReport;

/**
 * Mapper to fetch the popular hash tags for a stream.
 */
public class StreamPopularHashTagsDbMapper extends
        BaseArgDomainMapper<StreamPopularHashTagsRequest, StreamPopularHashTagsReport>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Number of hours to look through activities for popular hashtags.
     */
    private Integer popularHashTagWindowInHours;

    /**
     * Max number of popular hashtags to return.
     */
    private Integer maxNumberOfPopularHashTags;

    /**
     * Constructor.
     *
     * @param inPopularHashTagWindowInHours
     *            number of hours to look for popular hashtags
     * @param inMaxNumberOfPopularHashTags
     *            max number of popular hashtags to return
     */
    public StreamPopularHashTagsDbMapper(final Integer inPopularHashTagWindowInHours,
            final Integer inMaxNumberOfPopularHashTags)
    {
        popularHashTagWindowInHours = inPopularHashTagWindowInHours;
        maxNumberOfPopularHashTags = inMaxNumberOfPopularHashTags;
    }

    /**
     * Get the popular hashtags for the input Group/Person/Organization stream.
     *
     * @param inRequest
     *            type of stream and unique key of the entity stream to fetch hashtags for
     * @return the list of popular hashtags
     */
    public StreamPopularHashTagsReport execute(final StreamPopularHashTagsRequest inRequest)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 0 - popularHashTagWindowInHours);
        Date minActivityTime = calendar.getTime();

        if (log.isInfoEnabled())
        {
            log.info("Looking for " + maxNumberOfPopularHashTags + " popular hashtags for "
                    + inRequest.getStreamEntityScopeType() + " stream with id " + inRequest.getStreamEntityUniqueKey()
                    + " and activity date >= " + minActivityTime.toString());
        }

        Query query = getEntityManager().createQuery(
                "SELECT hashTag.content FROM StreamHashTag WHERE streamScopeType = :streamScopeType "
                        + "AND activityDate >= :activityDate AND streamEntityUniqueKey = :streamEntityUniqueKey "
                        + "GROUP BY hashTag.content ORDER BY COUNT(hashTag.content) DESC, hashTag.content ASC")
                .setParameter("streamScopeType", inRequest.getStreamEntityScopeType()).setParameter("activityDate",
                        minActivityTime).setParameter("streamEntityUniqueKey", inRequest.getStreamEntityUniqueKey());
        query.setMaxResults(maxNumberOfPopularHashTags);

        List<String> hashTags = query.getResultList();

        if (log.isInfoEnabled())
        {
            log.info("Found popular hashtags: " + hashTags + " for " + inRequest.getStreamEntityScopeType()
                    + " stream with id " + inRequest.getStreamEntityUniqueKey() + " and activity date >= "
                    + minActivityTime.toString());
        }

        Calendar now = Calendar.getInstance();
        return new StreamPopularHashTagsReport(hashTags, now.getTime());
    }
}
