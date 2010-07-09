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
package org.eurekastreams.server.service.actions.strategies;

import org.eurekastreams.server.domain.Organization;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * test for OrganizationLoaderAbstract class.
 * 
 */
public class OrganizationLoaderAbstractTest
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
    private OrganizationLoaderAbstract sut;

    /**
     * OrganizationLoader mock.
     */
    private OrganizationLoader decorated = context.mock(OrganizationLoader.class);

    /**
     * Organization mock.
     */
    private Organization organization = context.mock(Organization.class);

    /**
     * Pre-test setup.
     */
    @Before
    public void setUp()
    {
        sut = new Tester();
        sut.setOrganizationLoader(decorated);
    }

    /**
     * Test.
     */
    @Test
    public void testLoad()
    {
        context.checking(new Expectations()
        {
            {
                allowing(decorated).load(organization);
            }
        });

        sut.load(organization);
        context.assertIsSatisfied();
    }

    /**
     * Inner class that extends abstract for testing.
     * 
     */
    public class Tester extends OrganizationLoaderAbstract
    {

        /**
         * do nothing implementation.
         * 
         * @param inOrganization
         *            The organization.
         */
        @Override
        public void loadOrganization(final Organization inOrganization)
        {
            // do nothing.
        }

    }
}
