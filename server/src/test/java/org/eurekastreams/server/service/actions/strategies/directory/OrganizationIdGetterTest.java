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

import static org.junit.Assert.assertEquals;

import javax.persistence.NoResultException;

import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.hibernate.search.jpa.FullTextQuery;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for OrganizationIdGetter.
 */
public class OrganizationIdGetterTest
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
    private OrganizationIdGetter sut;

    /**
     * Search request builder mock.
     */
    private final ProjectionSearchRequestBuilder searchRequestBuilder = context
            .mock(ProjectionSearchRequestBuilder.class);

    /**
     * DB mapper mock.
     */
    private GetOrganizationsByShortNames orgByShortNameDBMapper = context.mock(GetOrganizationsByShortNames.class);

    /**
     * Test getIdentifier().
     */
    @Test
    public void testGetIdentifier()
    {
        final FullTextQuery query = context.mock(FullTextQuery.class);
        final long orgId = 18282L;
        final OrganizationModelView org = new OrganizationModelView();
        org.setEntityId(orgId);

        sut = new OrganizationIdGetter(searchRequestBuilder, orgByShortNameDBMapper);

        // setup context
        context.checking(new Expectations()
        {
            {
                one(searchRequestBuilder).buildQueryFromSearchText("abcdefg");
                will(returnValue(query));

                one(query).getSingleResult();
                will(returnValue(org));
            }
        });

        assertEquals(Long.toString(orgId), sut.getIdentifier("abcdefg"));
    }

    /**
     * Test getIdentifier().
     */
    @Test
    public void testGetIdentifierNotInIndex()
    {
        final FullTextQuery query = context.mock(FullTextQuery.class);
        final long orgId = 18282L;

        sut = new OrganizationIdGetter(searchRequestBuilder, orgByShortNameDBMapper);

        // setup context
        context.checking(new Expectations()
        {
            {
                one(searchRequestBuilder).buildQueryFromSearchText("abcdefg");
                will(returnValue(query));

                one(query).getSingleResult();
                will(throwException(new NoResultException()));

                one(orgByShortNameDBMapper).fetchId("abcdefg");
                will(returnValue(orgId));
            }
        });

        assertEquals(Long.toString(orgId), sut.getIdentifier("abcdefg"));
    }
}
