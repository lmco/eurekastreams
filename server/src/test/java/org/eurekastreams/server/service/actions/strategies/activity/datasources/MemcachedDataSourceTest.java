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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.MemcachedCache;
import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Memcache data source test.
 *
 */
public class MemcachedDataSourceTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * A map of search params and key generators.
     */
    private HashMap<String, MemcachedKeyGenerator> memcacheKeyGens = new HashMap<String, MemcachedKeyGenerator>();
    /**
     * The or collider.
     */
    private ListCollider orCollider = context.mock(ListCollider.class);
    /**
     * memcache.
     */
    private MemcachedCache cache = context.mock(MemcachedCache.class);

    /**
     * Key gens.
     */
    private MemcachedKeyGenerator followKeyGen = context.mock(MemcachedKeyGenerator.class);

    /**
     * System under test.
     */
    private MemcachedDataSource sut;

    /**
     * Request object.
     */
    private JSONObject request = new JSONObject();

    /**
     * The max we want this data source to return.
     */
    private static final int MAXITEMS = 10000;

    /**
     * The max count specified in the request.
     */
    private static final int COUNT = 10;

    /**
     * Set up an empty request and the SUT.
     */
    @Before
    public void setup()
    {
        request.put("count", COUNT);
        request.put("query", new JSONObject());

        memcacheKeyGens.put("followedBy", followKeyGen);
        sut = new MemcachedDataSource(memcacheKeyGens, orCollider, cache);
    }

    /**
     * Passing in an empty request triggers a return on everyone list.
     */
    @Test
    public void fetchForEveryone()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(cache).get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE);
                will(returnValue(1L));

                oneOf(cache).getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "1");

                oneOf(orCollider).collide(with(any(List.class)), with(any(List.class)), with(equalInternally(COUNT)));
            }
        });

        sut.fetch(request);
        context.assertIsSatisfied();
    }

    /**
     * If a request comes in with just a keyword its an everyone search, which lucene handles. DO NOTHING.
     */
    @Test
    public void fetchForEveryoneWithSearch()
    {
        ((JSONObject) request.get("query")).put("keywords", "eureka");


        sut.fetch(request);
        context.assertIsSatisfied();
    }


    /**
     * Following w/o keywords. Call the appropriate generator and trim to 10 results.
     */
    @Test
    public void fetchForFollowingWithOutKeyword()
    {
        ((JSONObject) request.get("query")).put("followedBy", "shawkings");

        final List<String> keys = new ArrayList<String>();
        keys.add("FOLLOWED:shawkings");

        context.checking(new Expectations()
        {
            {
                oneOf(followKeyGen).getKeys(with(any(JSONObject.class)));
                will(returnValue(keys));

                oneOf(cache).getList("FOLLOWED:shawkings");

                oneOf(orCollider).collide(with(any(List.class)), with(any(List.class)), with(equalInternally(COUNT)));
            }
        });

        sut.fetch(request);
        context.assertIsSatisfied();
    }

    /**
     * Following with a keyword. Were eventually going to collide with lucene so call the
     * appropriate generator and DO NOT trim the results.
     */
    @Test
    public void fetchForFollowingWithKeyword()
    {
        ((JSONObject) request.get("query")).put("followedBy", "shawkings");
        ((JSONObject) request.get("query")).put("keywords", "eureka");

        final List<String> keys = new ArrayList<String>();
        keys.add("FOLLOWED:shawkings");

        context.checking(new Expectations()
        {
            {
                oneOf(followKeyGen).getKeys(with(any(JSONObject.class)));
                will(returnValue(keys));

                oneOf(cache).getList("FOLLOWED:shawkings");

                oneOf(orCollider).collide(with(any(List.class)),
                        with(any(List.class)), with(equalInternally(MAXITEMS)));
            }
        });

        sut.fetch(request);
        context.assertIsSatisfied();
    }

}
