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
package org.eurekastreams.server.action.authorization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for IsOrganizationCoordinatorForAnyOrg class.
 * 
 */
public class IsOrganizationCoordinatorForAnyOrgTest
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
     * Mapper to get the root org.
     */
    private GetRootOrganizationIdAndShortName rootOrgMapper = context.mock(GetRootOrganizationIdAndShortName.class);
    /**
     * The mapper to get back all the coordinators of the root org and below.
     */
    private GetRecursiveOrgCoordinators recursiveOrgMapper = context.mock(GetRecursiveOrgCoordinators.class);

    /**
     * System under test.
     */
    private IsOrganizationCoordinatorForAnyOrg sut = new IsOrganizationCoordinatorForAnyOrg(recursiveOrgMapper,
            rootOrgMapper);

    /**
     * Test.
     */
    @Test
    public void authorizeWithAccess()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(rootOrgMapper).getRootOrganizationId();
                will(returnValue(0L));

                oneOf(recursiveOrgMapper).isOrgCoordinatorRecursively(4L, 0L);
                will(returnValue(true));
            }
        });

        assertTrue(sut.execute(4L));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void authorizeWithOutAccess()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(rootOrgMapper).getRootOrganizationId();
                will(returnValue(0L));

                oneOf(recursiveOrgMapper).isOrgCoordinatorRecursively(5L, 0L);
                will(returnValue(false));
            }
        });

        assertFalse(sut.execute(5L));
        context.assertIsSatisfied();
    }

}
