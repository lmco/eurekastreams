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

import org.eurekastreams.server.domain.Organization;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for IsRootOrganizationClassBridge.
 */
public class IsRootOrganizationClassBridgeTest
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
     * Test objectToString() with null input.
     */
    @Test
    public void testObjectToStringOnNull()
    {
        IsRootOrganizationClassBridge sut = new IsRootOrganizationClassBridge();
        assertNull(sut.objectToString(null));
    }

    /**
     * Test objectToString() with non-Organization input.
     */
    @Test
    public void testObjectToStringOnInvalidInput()
    {
        IsRootOrganizationClassBridge sut = new IsRootOrganizationClassBridge();
        assertNull(sut.objectToString(3L));
    }

    /**
     * Test objectToString() with root org.
     */
    @Test
    public void testObjectToStringOnRootOrg()
    {
        IsRootOrganizationClassBridge sut = new IsRootOrganizationClassBridge();
        final Organization rootOrg = context.mock(Organization.class);

        context.checking(new Expectations()
        {
            {
                exactly(2).of(rootOrg).getId();
                will(returnValue(3L));

                one(rootOrg).getParentOrganization();
                will(returnValue(rootOrg));

                // for logging
                allowing(rootOrg).getShortName();
                will(returnValue("foo"));
            }
        });

        assertEquals("true", sut.objectToString(rootOrg));

        context.assertIsSatisfied();
    }

    /**
     * Test objectToString() with non-root org.
     */
    @Test
    public void testObjectToStringOnNonRootOrg()
    {
        IsRootOrganizationClassBridge sut = new IsRootOrganizationClassBridge();
        final Organization parentOrg = context.mock(Organization.class, "parentOrg");
        final Organization childOrg = context.mock(Organization.class, "childOrg");

        context.checking(new Expectations()
        {
            {
                one(parentOrg).getId();
                will(returnValue(2L));

                one(childOrg).getId();
                will(returnValue(3L));

                one(childOrg).getParentOrganization();
                will(returnValue(parentOrg));
            }
        });

        assertNull(sut.objectToString(childOrg));

        context.assertIsSatisfied();
    }
}
