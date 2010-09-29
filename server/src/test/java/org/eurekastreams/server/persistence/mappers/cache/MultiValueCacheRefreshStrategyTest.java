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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for MultiValueCacheRefreshStrategy.
 * 
 */
public class MultiValueCacheRefreshStrategyTest
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
     * {@link Transformer}.
     */
    private Transformer<String, Serializable> suffixTransformer = context.mock(Transformer.class, "suffixTransformer");

    /**
     * {@link Transformer}.
     */
    private Transformer<String, Serializable> valueTransformer = context.mock(Transformer.class, "valueTransformer");

    /**
     * Cache prefix.
     */
    private String prefix = "prefix:";

    /**
     * Cache.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * Response list.
     */
    private List<String> responseList = new ArrayList<String>(Arrays.asList("foo", "bar"));

    /**
     * System under test.
     */
    private MultiValueCacheRefreshStrategy<String, String> sut = new MultiValueCacheRefreshStrategy<String, String>(
            prefix, suffixTransformer, valueTransformer, cache);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(suffixTransformer).transform("foo");
                will(returnValue("fooSuffixTransformed"));

                oneOf(suffixTransformer).transform("bar");
                will(returnValue("barSuffixTransformed"));

                oneOf(valueTransformer).transform("foo");
                will(returnValue("fooValueTransformed"));

                oneOf(valueTransformer).transform("bar");
                will(returnValue("barValueTransformed"));

                oneOf(cache).set("prefix:fooSuffixTransformed", "fooValueTransformed");

                oneOf(cache).set("prefix:barSuffixTransformed", "barValueTransformed");
            }
        });

        sut.refresh(null, responseList);
        context.assertIsSatisfied();
    }

}
