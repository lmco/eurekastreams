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
package org.eurekastreams.server.persistence.mappers.cache;

import junit.framework.Assert;

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.junit.Test;

/**
 * Test fixture for SharedResourceRequestToUniqueKeyTransformer.
 */
public class SharedResourceRequestToUniqueKeyTransformerTest
{
    /**
     * System under test.
     */
    private SharedResourceRequestToUniqueKeyTransformer sut = new SharedResourceRequestToUniqueKeyTransformer();

    /**
     * Test transforming a null request.
     */
    @Test
    public void testWithNullRequest()
    {
        Assert.assertNull(sut.transform(null));
    }

    /**
     * Test transforming a null unique key.
     */
    @Test
    public void testWithNullUniqueKey()
    {
        SharedResourceRequest request = new SharedResourceRequest();
        request.setUniqueKey(null);

        Assert.assertNull(sut.transform(request));
    }

    /**
     * Test transforming a valid request with a valid unique key.
     */
    @Test
    public void testWithSuccess()
    {
        SharedResourceRequest request = new SharedResourceRequest();
        request.setUniqueKey("HI");

        Assert.assertEquals("hi", sut.transform(request));
    }

}
