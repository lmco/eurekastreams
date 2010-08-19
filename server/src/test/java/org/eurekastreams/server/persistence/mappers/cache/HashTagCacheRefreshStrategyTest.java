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

import org.eurekastreams.server.domain.stream.HashTag;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for HashTagCacheRefreshStrategy.
 */
public class HashTagCacheRefreshStrategyTest
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
     * Mocked cache.
     */
    private final Cache cache = context.mock(Cache.class);

    /**
     * Test refresh with proper hashtag.
     */
    @Test
    public void testRefresh()
    {
        HashTagCacheRefreshStrategy sut = new HashTagCacheRefreshStrategy();
        sut.setCache(cache);

        final HashTag ht = new HashTag("#foo");

        context.checking(new Expectations()
        {
            {
                oneOf(cache).set(with(CacheKeys.HASH_TAG_BY_LOWERCASED_CONTENT + "#foo"), with(ht));
            }
        });

        sut.refresh("#foo", ht);

        context.assertIsSatisfied();
    }

    /**
     * Test refresh with uppercased hashtag.
     */
    @Test
    public void testRefreshWithUpperCasedHashTag()
    {
        HashTagCacheRefreshStrategy sut = new HashTagCacheRefreshStrategy();
        sut.setCache(cache);

        final HashTag ht = new HashTag("#foo");

        context.checking(new Expectations()
        {
            {
                oneOf(cache).set(with(CacheKeys.HASH_TAG_BY_LOWERCASED_CONTENT + "#foo"), with(ht));
            }
        });

        sut.refresh("#fOO", ht);

        context.assertIsSatisfied();
    }

    /**
     * Test refresh with missing pound/octathorpe.
     */
    @Test
    public void testRefreshWithMissingOctathorpe()
    {
        HashTagCacheRefreshStrategy sut = new HashTagCacheRefreshStrategy();
        sut.setCache(cache);

        final HashTag ht = new HashTag("#foo");

        context.checking(new Expectations()
        {
            {
                oneOf(cache).set(with(CacheKeys.HASH_TAG_BY_LOWERCASED_CONTENT + "#foo"), with(ht));
            }
        });

        sut.refresh("foo", ht);

        context.assertIsSatisfied();
    }
}
