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

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for SingleListValueCacheRefreshStrategy.
 */
public class SingleListValueCacheRefreshStrategyTest
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
     * Test refresh().
     */
    @Test
    public void testRefresh()
    {
        final CacheKeySuffixTransformer<String> transformer = context.mock(CacheKeySuffixTransformer.class);
        final Cache cache = context.mock(Cache.class);
        final String request = "abcdefg";
        final String suffix = "hijklmnop";
        final List<Long> response = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(transformer).transform(request);
                will(returnValue(suffix));

                oneOf(cache).setList("PREFIXhijklmnop", response);
            }
        });

        SingleListValueCacheRefreshStrategy<String> sut = new SingleListValueCacheRefreshStrategy<String>("PREFIX",
                transformer);
        sut.setCache(cache);
        sut.refresh(request, response);

        context.assertIsSatisfied();
    }
}
