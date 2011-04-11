/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Database mapper to get the DailyUsageSummary for a given date.
 */
public class GetDailyUsageSummaryByDateDbMapper extends BaseArgDomainMapper<Date, DailyUsageSummary>
{
    /**
     * Get the DailyUsageSummary for a specific day, or null if not available.
     * 
     * @param inDate
     *            the date to get DailyUsageSummary for
     * @return the DailyUsageSummary for the input day, or null if not available
     */
    @Override
    public DailyUsageSummary execute(final Date inDate)
    {
        List<DailyUsageSummary> results;
        Query q;

        q = getEntityManager().createQuery("FROM DailyUsageSummary WHERE usageDate = :usageDate");
        q.setParameter("usageDate", inDate);

        results = q.getResultList();
        if (results.size() > 0)
        {
            return results.get(0);
        }
        return null;
    }
}
