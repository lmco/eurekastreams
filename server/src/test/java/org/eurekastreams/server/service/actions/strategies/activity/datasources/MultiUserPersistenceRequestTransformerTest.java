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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the recipient transformer used by the persistence data source.
 */
@SuppressWarnings("unchecked")
public class MultiUserPersistenceRequestTransformerTest
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
     * Mapper to get a list of people ids by a list of account ids.
     */
    private DomainMapper<List<String>, List<Long>> getPeopleIdsByAccountIdsMapper = context.mock(DomainMapper.class,
            "getPeopleIdsByAccountIdsMapper");

    /**
     * Key to use.
     */
    private String key = "someKey";

    /**
     * System under test.
     */
    private MultiUserPersistenceRequestTransformer sut;

    /**
     * Setup test fixtures.
     */
    @Before
    public void setUp()
    {
        sut = new MultiUserPersistenceRequestTransformer(getPeopleIdsByAccountIdsMapper, key);
    }

    /**
     * Tests transformation with handled types.
     */
    @Test
    public void testTransform()
    {
        final JSONObject request = new JSONObject();

        final String personName1 = "personName1";
        final String personName2 = "personName2";

        final JSONObject personObj1 = new JSONObject();
        personObj1.accumulate("type", "PERSON");
        personObj1.accumulate("name", personName1);

        final JSONObject personObj2 = new JSONObject();
        personObj2.accumulate("type", "PERSON");
        personObj2.accumulate("name", personName2);

        JSONArray recipientArr = new JSONArray();
        recipientArr.add(personObj1);
        recipientArr.add(personObj2);

        request.accumulate(key, recipientArr);

        final Long personId1 = 4L;
        final Long personId2 = 5L;

        final ArrayList<String> expectedAccountIds = new ArrayList<String>();
        expectedAccountIds.add(personName1);
        expectedAccountIds.add(personName2);

        final List<Long> ids = new ArrayList<Long>();
        ids.add(personId1);
        ids.add(personId2);

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleIdsByAccountIdsMapper).execute(with(expectedAccountIds));
                will(returnValue(ids));
            }
        });

        List<Long> results = (List<Long>) sut.transform(request, 0L);

        Assert.assertEquals(2, results.size());
        Assert.assertEquals(personId1, results.get(0));
        Assert.assertEquals(personId2, results.get(1));

        context.assertIsSatisfied();
    }

    /**
     * Tests transformation with unhandled types.
     */
    @Test(expected = RuntimeException.class)
    public void testTransformUnhandledType()
    {
        final JSONObject request = new JSONObject();

        final JSONObject personObj = new JSONObject();
        personObj.accumulate("type", "NOTSET");
        personObj.accumulate("name", "something");

        JSONArray recipientArr = new JSONArray();
        recipientArr.add(personObj);

        request.accumulate(key, recipientArr);

        sut.transform(request, 0L);
    }
}
