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

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DeleteCacheKeys class.
 * 
 */
public class DeleteCacheKeysTest
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
     * Cache mock.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * System under test.
     */
    private DeleteCacheKeys sut;

    /**
     * Test.
     */
    @Test
    public void testExecute()
    {
        Set<String> params = new HashSet<String>(2);
        params.add("foo");
        params.add("bar");

        sut = new DeleteCacheKeys();
        sut.setCache(cache);

        context.checking(new Expectations()
        {
            {
                oneOf(cache).delete("foo");
                oneOf(cache).delete("bar");
            }
        });

        sut.execute(params);
        context.assertIsSatisfied();

    }

}
