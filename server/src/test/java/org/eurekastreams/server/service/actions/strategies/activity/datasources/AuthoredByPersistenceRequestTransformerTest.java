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

import junit.framework.Assert;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the authored by transformer used by the persistence data source.
 */
@SuppressWarnings("unchecked")
public class AuthoredByPersistenceRequestTransformerTest
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
     * Person mapper mock.
     */
    private DomainMapper<String, Long> getPersonIdByAccountIdMapper = context.mock(DomainMapper.class);

    /**
     * Group mapper mock.
     */
    private GetDomainGroupsByShortNames groupMapper = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * System under test.
     */
    private AuthoredByPersistenceRequestTransformer sut;

    /**
     * Setup test fixtures.
     */
    @Before
    public void setUp()
    {
        sut = new AuthoredByPersistenceRequestTransformer(getPersonIdByAccountIdMapper, groupMapper);
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

        final JSONObject personObj = new JSONObject();
        personObj.accumulate("type", "PERSON");
        personObj.accumulate("name", personName);

        final JSONObject groupObj = new JSONObject();
        groupObj.accumulate("type", "GROUP");
        groupObj.accumulate("name", groupName);

        JSONArray recipientArr = new JSONArray();
        recipientArr.add(personObj);
        recipientArr.add(groupObj);

        request.accumulate("authoredBy", recipientArr);

        final Long groupId = 6L;

        final Long personId = 5L;

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute(personName);
                will(returnValue(personId));

                oneOf(groupMapper).fetchId(groupName);
                will(returnValue(groupId));
            }
        });

        Assert.assertEquals("(p" + personId + " OR g" + groupId + ")", sut.transform(request, 0L));

        context.assertIsSatisfied();
    }

    /**
     * Tests transformation with one author.
     */
    @Test
    public void testOneAuthor()
    {
        final JSONObject request = new JSONObject();

        final String personName = "personName";

        final JSONObject personObj = new JSONObject();
        personObj.accumulate("type", "PERSON");
        personObj.accumulate("name", personName);

        JSONArray recipientArr = new JSONArray();
        recipientArr.add(personObj);

        request.accumulate("authoredBy", recipientArr);

        final Long personId = 5L;

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute(personName);
                will(returnValue(personId));
            }
        });

        Assert.assertEquals("(p" + personId + ")", sut.transform(request, 0L));

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

        request.accumulate("authoredBy", recipientArr);

        sut.transform(request, 0L);
    }
}
