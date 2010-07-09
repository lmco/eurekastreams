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
package org.eurekastreams.server.service.actions.strategies.directory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.server.domain.DomainGroup;
import org.hibernate.search.jpa.FullTextQuery;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for DomainGroupRoleDeterminer.
 */
public class DomainGroupRoleDeterminerTest
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
     * Mocked projectionSearchRequestBuilder.
     */
    private ProjectionSearchRequestBuilder searchRequestBuilder = context.mock(ProjectionSearchRequestBuilder.class);

    /**
     * System under test.
     */
    private DomainGroupRoleDeterminer sut;

    /**
     * Test isGroupCoordinatorOrFollower for a user that's not logged in.
     */
    @Test
    public void testIsGroupCoordinatorOrFollowerWithLoggedOutUser()
    {
        final long negativeLong = -3L;

        // lots of constructor asserts hers:
        context.checking(new Expectations()
        {
            {
                // setup the fields - we might not need any fields, but just in case, specify id
                List<String> fields = new ArrayList<String>();
                fields.add("id");
                one(searchRequestBuilder).setResultFields(fields);

                // set the entity types - just DomainGroups
                Class< ? >[] entityTypes = { DomainGroup.class };
                one(searchRequestBuilder).setResultTypes(entityTypes);
            }
        });

        // invoke
        sut = new DomainGroupRoleDeterminer(searchRequestBuilder);

        // assert
        assertFalse(sut.isGroupCoordinatorOrFollower(0L, 1L));
        assertFalse(sut.isGroupCoordinatorOrFollower(negativeLong, 1L));

        context.assertIsSatisfied();
    }

    /**
     * Test isGroupCoordinatorOrFollower.
     */
    @Test
    public void testIsGroupCoordinatorOrFollowerWithLoggedInUserWhoIsAFollowerOrCoordinator()
    {
        final long personId = 382821L;
        final long domainGroupId = 99182L;
        final FullTextQuery query = context.mock(FullTextQuery.class);

        // lots of constructor asserts hers:
        context.checking(new Expectations()
        {
            {
                // setup the fields - we might not need any fields, but just in case, specify id
                List<String> fields = new ArrayList<String>();
                fields.add("id");
                one(searchRequestBuilder).setResultFields(fields);

                // set the entity types - just DomainGroups
                Class< ? >[] entityTypes = { DomainGroup.class };
                one(searchRequestBuilder).setResultTypes(entityTypes);

                one(searchRequestBuilder).buildQueryFromNativeSearchString(
                        "+id:(99182) +followerAndCoordinatorIds:(382821)");
                will(returnValue(query));

                one(query).getResultSize();
                will(returnValue(1));
            }
        });

        // invoke
        sut = new DomainGroupRoleDeterminer(searchRequestBuilder);

        // assert
        assertTrue(sut.isGroupCoordinatorOrFollower(personId, domainGroupId));

        context.assertIsSatisfied();
    }

    /**
     * Test isGroupCoordinatorOrFollower.
     */
    @Test
    public void testIsGroupCoordinatorOrFollowerWithLoggedInUserWhoIsNotAFollowerOrCoordinator()
    {
        final long personId = 382821L;
        final long domainGroupId = 99182L;
        final FullTextQuery query = context.mock(FullTextQuery.class);

        // lots of constructor asserts hers:
        context.checking(new Expectations()
        {
            {
                // setup the fields - we might not need any fields, but just in case, specify id
                List<String> fields = new ArrayList<String>();
                fields.add("id");
                one(searchRequestBuilder).setResultFields(fields);

                // set the entity types - just DomainGroups
                Class< ? >[] entityTypes = { DomainGroup.class };
                one(searchRequestBuilder).setResultTypes(entityTypes);

                one(searchRequestBuilder).buildQueryFromNativeSearchString(
                        "+id:(99182) +followerAndCoordinatorIds:(382821)");
                will(returnValue(query));

                one(query).getResultSize();
                will(returnValue(0));
            }
        });

        // invoke
        sut = new DomainGroupRoleDeterminer(searchRequestBuilder);

        // assert
        assertFalse(sut.isGroupCoordinatorOrFollower(personId, domainGroupId));

        context.assertIsSatisfied();
    }
}
