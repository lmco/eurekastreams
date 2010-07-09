/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test class for the GetCompositeStreamById mapper.
 *
 */
public class GetCompositeStreamByIdTest extends CachedMapperTest
{
    /**
     * Test id that is valid for the dataset.
     */
    private static final Long TEST_VALID_STREAMVIEW_ID = 883L;

    /**
     * Test id that is invalid for the dataset.
     */
    private static final Long TEST_INVALID_STREAMVIEW_ID = 71L;

    /**
     * local instance of the StreamCacheLoader, only needed for initializing the cache before this test suite.
     */
    @Autowired
    private StreamCacheLoader streamLoader;

    /**
     * System under test.
     */
    @Autowired
    private GetCompositeStreamById sut;

    /**
     * Preparation for the test suite.
     */
    @Before
    public void setup()
    {
        streamLoader.initialize();
    }

    /**
     * Test the execution with a valid id.
     */
    @Test
    public void testExecuteWithValidId()
    {
        StreamView result = sut.execute(TEST_VALID_STREAMVIEW_ID);
        assertNotNull(result);
    }

    /**
     * Test the execution with an invalid id.
     */
    @Test
    public void testExecuteWithInValidId()
    {
        StreamView result = sut.execute(TEST_INVALID_STREAMVIEW_ID);
        assertNull(result);
    }
}
