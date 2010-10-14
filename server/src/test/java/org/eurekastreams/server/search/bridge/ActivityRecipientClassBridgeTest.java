/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.search.bridge;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ActivityRecipientClassBridge.
 */
public class ActivityRecipientClassBridgeTest
{
    /**
     * Context for building mock objects.
     */
    private Mockery context;

    /**
     * Account id of the person we're testing for.
     */
    private String testPersonAccountId = "abcdefg";

    /**
     * Short name of the domain group we're testing for.
     */
    private String testDomainGroupShortName = "hijklmno";

    /**
     * Mocked GetDomainGroupsByShortNames.
     */
    private GetDomainGroupsByShortNames getDomainGroupsByShortNamesMock;

    /**
     * Mapper to lookup person id by account id.
     */
    private static DomainMapper<String, Long> getPersonIdByAccountIdMapper;

    /**
     * ID of the test person.
     */
    private final long testPersonId = 382L;

    /**
     * ID of the test group.
     */
    private final long testDomainGroupId = 8784L;

    /**
     * System under test.
     */
    private ActivityRecipientClassBridge sut = new ActivityRecipientClassBridge();

    /**
     * Class teardown method.
     */
    @After
    public void teardown()
    {
        ActivityRecipientClassBridge.setGetDomainGroupsByShortNames(null);
        ActivityRecipientClassBridge.setGetPersonIdByAccountIdMapper(null);
        context = null;
    }

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        context = new JUnit4Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

        getDomainGroupsByShortNamesMock = context.mock(GetDomainGroupsByShortNames.class);
        getPersonIdByAccountIdMapper = context.mock(DomainMapper.class);

        ActivityRecipientClassBridge.setGetPersonIdByAccountIdMapper(getPersonIdByAccountIdMapper);
        ActivityRecipientClassBridge.setGetDomainGroupsByShortNames(getDomainGroupsByShortNamesMock);

        context.checking(new Expectations()
        {
            {
                allowing(getPersonIdByAccountIdMapper).execute(testPersonAccountId);
                will(returnValue(testPersonId));
            }
        });
    }

    /**
     * Test objectToString for group.
     */
    @Test
    public void testObjectToStringForGroupFromActivityObject()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(getDomainGroupsByShortNamesMock).fetchId(testDomainGroupShortName);
                will(returnValue(testDomainGroupId));
            }
        });

        Activity activity = new Activity();
        activity.setRecipientStreamScope(new StreamScope(ScopeType.GROUP, testDomainGroupShortName));
        assertEquals("g" + testDomainGroupId, sut.objectToString(activity));
        context.assertIsSatisfied();
    }

    /**
     * Test objectToString for person.
     */
    @Test
    public void testObjectToStringForPersonFromActivityObject()
    {
        Activity activity = new Activity();
        activity.setRecipientStreamScope(new StreamScope(ScopeType.PERSON, testPersonAccountId));
        assertEquals("p" + testPersonId, sut.objectToString(activity));
        context.assertIsSatisfied();
    }

    /**
     * Test that the appropriate exception is thrown when the bridge doesn't have a person cache.
     */
    @Test(expected = RuntimeException.class)
    public void testObjectToStringWithoutPeopleByAccountIdMapper()
    {
        ActivityRecipientClassBridge.setGetPersonIdByAccountIdMapper(null);
        sut.objectToString(new Activity());
    }

    /**
     * Test that the appropriate exception is thrown when the bridge doesn't have a domain group cache.
     */
    @Test(expected = RuntimeException.class)
    public void testObjectToStringWithoutDomainGroupCache()
    {
        ActivityRecipientClassBridge.setGetDomainGroupsByShortNames(null);
        sut.objectToString(new Activity());
    }

    /**
     * Test objectToString with an invalid input type.
     */
    @Test(expected = RuntimeException.class)
    public void testObjectToStringForInvalidType()
    {
        sut.objectToString(new PersonModelView());
    }
}
