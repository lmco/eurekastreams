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
import static org.junit.Assert.assertNull;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for DomainGroupPeopleIdClassBridge.
 */
public class DomainGroupPeopleIdClassBridgeTest
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
     * System under test.
     */
    private DomainGroupPeopleIdClassBridge sut = new DomainGroupPeopleIdClassBridge();

    /**
     * Test objectToString with null input.
     */
    @Test
    public void testObjectToStringOnNullInput()
    {
        assertNull(sut.objectToString(null));
    }

    /**
     * Test objectToString with invalid input.
     */
    @Test
    public void testObjectToStringOnInvalidInput()
    {
        assertNull(sut.objectToString(0L));
    }

    /**
     * Test objectToString with null mapper.
     */
    @Test(expected = IllegalStateException.class)
    public void testObjectToStringOnNullMapper()
    {
        DomainGroupPeopleIdClassBridge.setDomainGroupMapper(null);
        sut.objectToString(new DomainGroup());
    }

    /**
     * Test objectToString with null mapper.
     */
    @Test
    public void testObjectToStringOnSuccess()
    {
        final DomainGroup group = context.mock(DomainGroup.class);
        final DomainGroupMapper groupMapper = context.mock(DomainGroupMapper.class);
        final Long[] personIds = { 1L, 2L, 5L, 8L, 10L, 382L };

        context.checking(new Expectations()
        {
            {
                one(groupMapper).getFollowerAndCoordinatorPersonIds(group);
                will(returnValue(personIds));
            }
        });

        DomainGroupPeopleIdClassBridge.setDomainGroupMapper(groupMapper);
        String result = sut.objectToString(group);
        context.assertIsSatisfied();
        
        assertEquals("1 2 5 8 10 382", result);
    }
}
