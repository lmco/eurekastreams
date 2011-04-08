/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.search.modelview.UsageMetricSummaryDTO;

/**
 * Mapper to get UsageMetricSummary.
 * 
 */
public class GetUsageMetricSummaryDbMapper extends BaseArgDomainMapper<Integer, UsageMetricSummaryDTO>
{
    /**
     * Get Summary usage metrics for last X number of days.
     * 
     * @param inRequest
     *            number of days to get metrics summary for.
     * @return {@link UsageMetricSummaryDTO} representing given time period.
     */
    @Override
    public UsageMetricSummaryDTO execute(final Integer inRequest)
    {
        // TODO Generate the real info.
        return new UsageMetricSummaryDTO();
    }

}
