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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;

/**
 * Transformer to convert a UsageMetricStreamSummaryRequest to the stream scope id, or 0 for null.
 */
public class UsageMetricStreamSummaryRequestToStreamScopeIdTransformer implements
        Transformer<UsageMetricStreamSummaryRequest, String>
{
    /**
     * Transform the input request to the stream scope id, or 0 for null.
     * 
     * @param inRequest
     *            the request
     * @return the stream scope id, or 0 for null
     */
    @Override
    public String transform(final UsageMetricStreamSummaryRequest inRequest)
    {
        if (inRequest.getStreamRecipientStreamScopeId() == null)
        {
            return "0";
        }
        else
        {
            return inRequest.getStreamRecipientStreamScopeId().toString();
        }
    }
}
