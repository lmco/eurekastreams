/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Sort;
import org.hibernate.search.jpa.FullTextQuery;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.ResourceSortCriteria;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Test fixture for OrgChildrenGetter strategy.
 */
public class OrgChildrenGetterTest
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
     * Projection search request builder to use.
     */
    private final ProjectionSearchRequestBuilder searchRequestBuilder = context
            .mock(ProjectionSearchRequestBuilder.class);

    /**
     * The SortFieldBuilder to use.
     */
    private final SortFieldBuilder sortFieldBuilder = context.mock(SortFieldBuilder.class);

    /**
     * Query builder.
     */
    private final DirectorySearchLuceneQueryBuilder queryBuilder = context
            .mock(DirectorySearchLuceneQueryBuilder.class);

    /**
     * The resource sort criteria to use.
     */
    private final ResourceSortCriteria sortCriteria = context.mock(ResourceSortCriteria.class);
    

    /**
     * Test getOrgChildren() not recursive.
     */
    @Test
    public void testGetOrgChildrenNonRecursive()
    {
        final FullTextQuery query = context.mock(FullTextQuery.class);
        final String searchText = "foo";
        final Sort sort = context.mock(Sort.class);
        final int from = 5;
        final int to = 9;
        final int total = 99;
        final List<PersonModelView> resultList = new ArrayList<PersonModelView>();

        final String nativeQuery = "foobar";
        OrgChildrenGetter<PersonModelView> sut = new OrgChildrenGetter<PersonModelView>(queryBuilder,
                searchRequestBuilder, sortFieldBuilder, false);

        context.checking(new Expectations()
        {
            {
                one(queryBuilder).buildNativeQuery(searchText, "", "", 8);
                will(returnValue(nativeQuery));

                one(searchRequestBuilder).buildQueryFromNativeSearchString(nativeQuery);
                will(returnValue(query));

                one(sortFieldBuilder).getSort(sortCriteria);
                will(returnValue(sort));

                one(query).setSort(sort);

                one(searchRequestBuilder).setPaging(query, from, to);

                one(query).getResultList();
                will(returnValue(resultList));

                one(query).getResultSize();
                will(returnValue(total));
            }
        });

        PagedSet<PersonModelView> results = sut.getOrgChildren(searchText, from, to, sortCriteria, 8);

        assertEquals(from, results.getFromIndex());
        assertEquals(to, results.getToIndex());
        assertEquals(total, results.getTotal());
        assertSame(resultList, results.getPagedSet());

        context.assertIsSatisfied();
    }

    /**
     * Test getOrgChildren() recursive.
     */
    @Test
    public void testGetOrgChildrenRecursive()
    {
        final FullTextQuery query = context.mock(FullTextQuery.class);
        final String searchText = "foo";
        final Sort sort = context.mock(Sort.class);
        final int from = 5;
        final int to = 9;
        final int total = 99;
        final List<PersonModelView> resultList = new ArrayList<PersonModelView>();

        final String nativeQuery = "foobar";
        OrgChildrenGetter<PersonModelView> sut = new OrgChildrenGetter<PersonModelView>(queryBuilder,
                searchRequestBuilder, sortFieldBuilder, true);

        context.checking(new Expectations()
        {
            {
                one(queryBuilder).buildNativeQuery(searchText, "", searchText, 9);
                will(returnValue(nativeQuery));

                one(searchRequestBuilder).buildQueryFromNativeSearchString(nativeQuery);
                will(returnValue(query));

                one(sortFieldBuilder).getSort(sortCriteria);
                will(returnValue(sort));

                one(query).setSort(sort);

                one(searchRequestBuilder).setPaging(query, from, to);

                one(query).getResultList();
                will(returnValue(resultList));

                one(query).getResultSize();
                will(returnValue(total));
            }
        });

        PagedSet<PersonModelView> results = sut.getOrgChildren(searchText, from, to, sortCriteria, 9);

        assertEquals(from, results.getFromIndex());
        assertEquals(to, results.getToIndex());
        assertEquals(total, results.getTotal());
        assertSame(resultList, results.getPagedSet());

        context.assertIsSatisfied();
    }
}
