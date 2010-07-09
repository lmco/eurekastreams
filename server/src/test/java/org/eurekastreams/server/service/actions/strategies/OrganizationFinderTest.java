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
package org.eurekastreams.server.service.actions.strategies;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for person finder.
 * 
 */
public class OrganizationFinderTest
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
     * Mock.
     */
    private OrganizationMapper mapper = context.mock(OrganizationMapper.class);

    /**
     * Mock.
     */
    private Principal user = context.mock(Principal.class);

    /**
     * Sut.
     */
    private OrganizationFinder sut = new OrganizationFinder(mapper);

    /**
     * Mock.
     */
    private Organization org = context.mock(Organization.class);

    /**
     * Test.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void findEntityById() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findById(3L);
                will(returnValue(org));
            }
        });

        sut.findEntity(user, 3L);
        context.assertIsSatisfied();

    }

    /**
     * Test.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void findEntityIfNotAuthAccountId() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findByShortName("org");
                will(returnValue(org));
            }
        });

        sut.findEntity(user, "org");
        context.assertIsSatisfied();

    }
}
