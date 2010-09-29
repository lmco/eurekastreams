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
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for MultiRefreshCacheRefreshStrategy.
 * 
 */
public class MultiRefreshCacheRefreshStrategyTest
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
     * {@link RefreshStrategy}.
     */
    @SuppressWarnings("unchecked")
    private RefreshStrategy refreshStrategy1 = context.mock(RefreshStrategy.class, "strat1");

    /**
     * {@link RefreshStrategy}.
     */
    @SuppressWarnings("unchecked")
    private RefreshStrategy refreshStrategy2 = context.mock(RefreshStrategy.class, "strat2");

    /**
     * List of {@link RefreshStrategy}s.
     */
    @SuppressWarnings("unchecked")
    private List<RefreshStrategy> strategies = new ArrayList<RefreshStrategy>(Arrays.asList(refreshStrategy1,
            refreshStrategy2));

    /**
     * System under test.
     */
    @SuppressWarnings("unchecked")
    private MultiRefreshCacheRefreshStrategy sut = new MultiRefreshCacheRefreshStrategy(strategies);

    /**
     * Test.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(refreshStrategy1).refresh(5L, 6L);
                oneOf(refreshStrategy2).refresh(5L, 6L);
            }
        });
        sut.refresh(5L, 6L);
        context.assertIsSatisfied();
    }

}
