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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for HideResourceActivityMapper.
 * 
 */
public class HideResourceActivityMapperTest extends MapperTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private HideResourceActivityMapper sut = new HideResourceActivityMapper();

    /**
     * Cache mock.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut.setEntityManager(getEntityManager());
        sut.setCache(cache);
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final Long activityId = 6789L;

        Activity a = (Activity) getEntityManager().createQuery("FROM Activity WHERE id = :activityId").setParameter(
                "activityId", activityId).getSingleResult();

        assertTrue(a.getShowInStream());

        context.checking(new Expectations()
        {
            {
                oneOf(cache).removeFromList(CacheKeys.EVERYONE_ACTIVITY_IDS, activityId);

                oneOf(cache).delete(CacheKeys.ACTIVITY_BY_ID + activityId);

                oneOf(cache).delete(CacheKeys.ACTIVITY_SECURITY_BY_ID + activityId);
            }
        });

        sut.execute(activityId);

        getEntityManager().flush();
        getEntityManager().clear();

        a = (Activity) getEntityManager().createQuery("FROM Activity WHERE id = :activityId").setParameter(
                "activityId", activityId).getSingleResult();

        assertFalse(a.getShowInStream());

        context.assertIsSatisfied();

    }
}
