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

/**
 * Request object to get the usage metric summary over a period of time, optionally for a specific stream.
 */
public class UsageMetricStreamSummaryRequest implements Serializable
{
    /**
     * Serial version UUID.
     */
    private static final long serialVersionUID = -3740790360933643576L;

    /**
     * The stream's recipient stream scope id, or null if we should grab data for all streams.
     */
    private Long streamRecipientStreamScopeId;

    /**
     * Number of days to look back.
     */
    private Integer numberOfDays;

    /**
     * Empty constructor for serialization.
     */
    public UsageMetricStreamSummaryRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inNumberOfDays
     *            the number of days to get metric info for
     * @param inStreamRecipientStreamScopeId
     *            the stream scope id to limit the info to, or null for all streams
     */
    public UsageMetricStreamSummaryRequest(final Integer inNumberOfDays, final Long inStreamRecipientStreamScopeId)
    {
        numberOfDays = inNumberOfDays;
        streamRecipientStreamScopeId = inStreamRecipientStreamScopeId;
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
     * @return the numberOfDays
     */
    public Integer getNumberOfDays()
    {
        return numberOfDays;
    }

    /**
     * @param inNumberOfDays
     *            the numberOfDays to set
     */
    public void setNumberOfDays(final Integer inNumberOfDays)
    {
        numberOfDays = inNumberOfDays;
    }

}
