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
package org.eurekastreams.server.service.actions.requests;

import java.io.Serializable;
import java.util.Date;

/**
 * Request for getting usage metrics for a particular day and optionally just for a stream.
 */
public class UsageMetricDailyStreamInfoRequest implements Serializable
{
    /**
     * Serial version UUID.
     */
    private static final long serialVersionUID = 1139563902829666212L;

    /**
     * The stream's recipient stream scope id, or null if we should grab data for all streams.
     */
    private Long streamRecipientStreamScopeId;

    /**
     * the date we want metrics info from.
     */
    private Date metricsDate;

    /**
     * Empty constructor for serialization.
     */
    public UsageMetricDailyStreamInfoRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inMetricsDate
     *            the date to get metrics for
     * @param inStreamRecipientStreamScopeId
     *            if null, get data for all streams, else just for the stream with this stream scope id
     */
    public UsageMetricDailyStreamInfoRequest(final Date inMetricsDate, final Long inStreamRecipientStreamScopeId)
    {
        streamRecipientStreamScopeId = inStreamRecipientStreamScopeId;
        metricsDate = inMetricsDate;
    }

    /**
     * @return the streamRecipientStreamScopeId
     */
    public Long getStreamRecipientStreamScopeId()
    {
        return streamRecipientStreamScopeId;
    }

    /**
     * @param inStreamRecipientStreamScopeId
     *            the streamRecipientStreamScopeId to set
     */
    public void setStreamRecipientStreamScopeId(final Long inStreamRecipientStreamScopeId)
    {
        streamRecipientStreamScopeId = inStreamRecipientStreamScopeId;
    }

    /**
     * @return the metricsDate
     */
    public Date getMetricsDate()
    {
        return metricsDate;
    }

    /**
     * @param inMetricsDate
     *            the metricsDate to set
     */
    public void setMetricsDate(final Date inMetricsDate)
    {
        metricsDate = inMetricsDate;
    }

}
