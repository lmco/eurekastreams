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
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
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
public class PersistenceDataSourceTest
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
    private HashMap<String, DomainMapper<Object, List<Long>>> mappers = new HashMap<String, DomainMapper<Object, List<Long>>>();

    private HashMap<String, PersistenceDataSourceRequestTransformer> transformers = new HashMap<String, PersistenceDataSourceRequestTransformer>();
    /**
     * The or collider.
     */
    private ListCollider orCollider = context.mock(ListCollider.class);


    /**
     * Key gens.
     */
    private DomainMapper<Object, List<Long>> followedMapper = context.mock(DomainMapper.class);

    private DomainMapper<Object, List<Long>> everyoneMapper = context.mock(DomainMapper.class, "everyone");

    private PersistenceDataSourceRequestTransformer transformer = context.mock(PersistenceDataSourceRequestTransformer.class);

    /**
     * System under test.
     */
    private PersistenceDataSource sut;

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

        mappers.put("followedBy", followedMapper);
        transformers.put("followedBy", transformer);
        sut = new PersistenceDataSource(everyoneMapper, mappers, transformers, orCollider);
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
                List<Long> everyoneIds = new ArrayList<Long>();

                oneOf(everyoneMapper).execute(null);
                will(returnValue(everyoneIds));
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
                oneOf(transformer).transform(with(any(JSONObject.class)));
                will(returnValue(2L));
                oneOf(followedMapper).execute(2L);
                will(returnValue(keys));



                oneOf(orCollider).collide(with(any(List.class)), with(any(List.class)), with(equalInternally(COUNT)));
            }
        });

        sut.fetch(request);
        context.assertIsSatisfied();
    }

    /**
     * Following with a keyword. Were eventually going to collide with lucene so call the appropriate generator and DO
     * NOT trim the results.
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
                oneOf(transformer).transform(with(any(JSONObject.class)));
                will(returnValue(2L));
                oneOf(followedMapper).execute(2L);
                will(returnValue(keys));

                oneOf(orCollider).collide(with(any(List.class)),
                        with(any(List.class)), with(equalInternally(MAXITEMS)));

            }
        });

        sut.fetch(request);
        context.assertIsSatisfied();
    }

    /**
     * Following with a keyword and no handled query terms - should return null.
     */
    @Test
    public void fetchForEveryoneWithKeyword()
    {
        ((JSONObject) request.get("query")).put("keywords", "eureka");

        assertNull(sut.fetch(request));
        context.assertIsSatisfied();
    }
}
