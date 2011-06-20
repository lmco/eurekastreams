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

import junit.framework.Assert;

import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;
import org.junit.Test;

/**
 * Test fixture for UsageMetricStreamSummaryRequestToStreamScopeIdTransformer.
 */
public class UsageMetricStreamSummaryRequestToStreamScopeIdTransformerTest
{
    /**
     * System under test.
     */
    private UsageMetricStreamSummaryRequestToStreamScopeIdTransformer sut = //
    new UsageMetricStreamSummaryRequestToStreamScopeIdTransformer();

    /**
     * Test when the id is null.
     */
    @Test
    public void testWhenNull()
    {
        Assert.assertEquals("0", sut.transform(new UsageMetricStreamSummaryRequest(5, null)));
    }

    /**
     * Test when the id is not null.
     */
    @Test
    public void testWhenNotNull()
    {
        Assert.assertEquals("3", sut.transform(new UsageMetricStreamSummaryRequest(5, 3L)));
    }
}
