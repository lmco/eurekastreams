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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.strategies.DescendantOrganizationStrategy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the org activity ids DB mapper.
 */
public class OrgActivityIdsDbMapperTest extends MapperTest
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
    private OrgActivityIdsDbMapper sut = null;

    /**
     * Stream mapper.
     */
    private DescendantOrganizationStrategy descendantOrganizationStrategy = context.mock(
            DescendantOrganizationStrategy.class, "descendantOrganizationStrategy");

    /**
     * Setup test fixtures.
     */
    @Before
    public void before()
    {
        sut = new OrgActivityIdsDbMapper();
        sut.setEntityManager(getEntityManager());
        sut.setDescendantOrganizationStrategy(descendantOrganizationStrategy);
    }

    /**
     * Test the mapper with children.
     */
    @Test
    public void testWithChildren()
    {
        final long orgId = 5L;

        final String children = "6,7";

        context.checking(new Expectations()
        {
            {
                oneOf(descendantOrganizationStrategy).getDescendantOrganizationIdsForJpql(with(equal(orgId)),
                        with(any(HashMap.class)));
                will(returnValue(children));
            }
        });

        List<Long> results = sut.execute(orgId);
        
        Assert.assertEquals(5, results.size());

        context.assertIsSatisfied();
    }

    /**
     * Test the mapper without children.
     */
    @Test
    public void testWithoutChildren()
    {
        final long orgId = 5L;

        final String children = "";

        context.checking(new Expectations()
        {
            {
                oneOf(descendantOrganizationStrategy).getDescendantOrganizationIdsForJpql(with(equal(orgId)),
                        with(any(HashMap.class)));
                will(returnValue(children));
            }
        });

        List<Long> results = sut.execute(orgId);

        Assert.assertEquals(2, results.size());
        
        context.assertIsSatisfied();
    }
}
