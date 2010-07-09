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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertSame;

import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.persistence.mappers.GetOrganizationTreeDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetOrganizationTreeExecution class.
 * 
 */
public class GetOrganizationTreeExecutionTest
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
    private GetOrganizationTreeExecution sut;

    /**
     * Organization tree DAO.
     */
    private GetOrganizationTreeDTO organizationTreeDAO = context.mock(GetOrganizationTreeDTO.class);

    /**
     * Organization tree DTO.
     */
    private OrganizationTreeDTO orgTreeDTO = context.mock(OrganizationTreeDTO.class);

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new GetOrganizationTreeExecution(organizationTreeDAO);
    }

    /**
     * Test.
     */
    @Test
    public final void executeTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(organizationTreeDAO).execute();
                will(returnValue(orgTreeDTO));
            }
        });

        assertSame(orgTreeDTO, sut.execute(null));
        context.assertIsSatisfied();
    }

}
