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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests mapping activity security information from the cache.
 */
public class BulkActivitySecurityMapperTest
{
    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private static BulkActivitySecurityMapper sut;

    /**
     * Cache fed into the loader.
     */
    private static Cache cache = CONTEXT.mock(Cache.class);

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setup()
    {
        sut = new BulkActivitySecurityMapper();
        sut.setCache(cache);
    }

    /**
     * Tests executing with complete results.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteCompleteResults()
    {
        final Map<String, ActivitySecurityDTO> activities = new HashMap<String, ActivitySecurityDTO>();
        activities.put(CacheKeys.ACTIVITY_SECURITY_BY_ID + 1L, new ActivitySecurityDTO());
        activities.put(CacheKeys.ACTIVITY_SECURITY_BY_ID + 2L, new ActivitySecurityDTO());

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(cache).multiGet(with(any(Collection.class)));
                will(returnValue(activities));
            }
        });

        PartialMapperResponse<List<Long>, Collection<ActivitySecurityDTO>> results = sut.execute(Arrays.asList(1L, 2L));

        Assert.assertFalse(results.hasUnhandledRequest());
        Assert.assertNull(results.getUnhandledRequest());
        
        CONTEXT.assertIsSatisfied();
    }

    /**
     * Tests executing with partial results.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecutePartialResults()
    {
        final Map<String, ActivitySecurityDTO> activities = new HashMap<String, ActivitySecurityDTO>();
        activities.put(CacheKeys.ACTIVITY_SECURITY_BY_ID + 1L, new ActivitySecurityDTO());

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(cache).multiGet(with(any(Collection.class)));
                will(returnValue(activities));
            }
        });

        PartialMapperResponse<List<Long>, Collection<ActivitySecurityDTO>> results = sut.execute(Arrays.asList(1L, 2L));

        Assert.assertTrue(results.hasUnhandledRequest());
        Assert.assertEquals(1, results.getUnhandledRequest().size());
        
        CONTEXT.assertIsSatisfied();
    }
}
