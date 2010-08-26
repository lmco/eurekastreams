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
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the recipient transformer used by the persistence data source.
 */
@SuppressWarnings("unchecked")
public class RecipientPersistenceRequestTransformerTest
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
     * Stream ID mapper mock.
     */
    private DomainMapper<Map<Long, EntityType>, List<Long>> streamIdMapper = context.mock(DomainMapper.class);

    /**
     * Person mapper mock.
     */
    private GetPeopleByAccountIds personMapper = context.mock(GetPeopleByAccountIds.class);

    /**
     * Group mapper mock.
     */
    private GetDomainGroupsByShortNames groupMapper = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * System under test.
     */
    private RecipientPersistenceRequestTransformer sut;

    /**
     * Setup test fixtures.
     */
    @Before
    public void setUp()
    {
        sut = new RecipientPersistenceRequestTransformer(personMapper, groupMapper, streamIdMapper);
    }

    /**
     * Tests transformation with handled types.
     */
    @Test
    public void testTransform()
    {
        final JSONObject request = new JSONObject();

        final String personName = "personName";
        final String groupName = "groupName";
        final String orgName = "orgName";

        final JSONObject personObj = new JSONObject();
        personObj.accumulate("type", "PERSON");
        personObj.accumulate("name", personName);

        final JSONObject groupObj = new JSONObject();
        groupObj.accumulate("type", "GROUP");
        groupObj.accumulate("name", groupName);

        JSONArray recipientArr = new JSONArray();
        recipientArr.add(personObj);
        recipientArr.add(groupObj);

        request.accumulate("recipient", recipientArr);

        final ArrayList<Long> retVal = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).fetchId(personName);
                oneOf(groupMapper).fetchId(groupName);

                oneOf(streamIdMapper).execute(with(any(Map.class)));
                will(returnValue(retVal));
            }
        });

        sut.transform(request, 0L);

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

        request.accumulate("recipient", recipientArr);

        sut.transform(request, 0L);
    }
}
