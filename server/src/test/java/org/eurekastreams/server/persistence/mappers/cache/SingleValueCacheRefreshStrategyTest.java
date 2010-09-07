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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for SingleValueCacheRefreshStrategy.
 */
public class SingleValueCacheRefreshStrategyTest
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
     * Cache key suffix transformer.
     */
    private final CacheKeySuffixTransformer<Object> cacheKeySuffixTransformer = context
            .mock(CacheKeySuffixTransformer.class);

    /**
     * Cache.
     */
    private final Cache cache = context.mock(Cache.class);

    /**
     * Test refresh().
     */
    @Test
    public void testRefresh()
    {
        final Object request = new Object();
        final Object response = new Object();
        final String prefix = "PREFIX:";
        final String suffix = "SUFFIX";

        SingleValueCacheRefreshStrategy<Object, Object> sut = new SingleValueCacheRefreshStrategy<Object, Object>(
                prefix, cacheKeySuffixTransformer);
        sut.setCache(cache);

        context.checking(new Expectations()
        {
            {
                oneOf(cacheKeySuffixTransformer).transform(request);
                will(returnValue(suffix));

                oneOf(cache).set(prefix + suffix, response);
            }
        });

        sut.refresh(request, response);
        context.assertIsSatisfied();
    }
}
