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

import org.eurekastreams.commons.search.LuceneFieldBooster;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for DirectorySearchLuceneQueryBuilder.
 */
public class DirectorySearchLuceneQueryBuilderTest
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
     * Field booster.
     */
    private LuceneFieldBooster fieldBooster = context.mock(LuceneFieldBooster.class);

    /**
     * Field boost factor.
     */
    private final int fieldBoostFactor = 500;

    /**
     * Org id getter.
     */
    private OrganizationIdGetter orgIdGetter = context.mock(OrganizationIdGetter.class);

    /**
     * System under test.
     */
    private DirectorySearchLuceneQueryBuilder sut;

    /**
     * Search query used for all entities.
     */
    private String allEntitiesSearch = "+(lastName:(%1$s)^2 name:(%1$s) overview:(%1$s) biography:(%1$s) "
            + "jobs:(%1$s) background:(%1$s) education:(%1$s) title:(%1$s) capabilities:(%1$s))";

    /**
     * Search query after 'background' was boosted by 500.
     */
    private String boostedFieldQueryMask = "+(lastName:(%1$s)^2 name:(%1$s) overview:(%1$s) biography:(%1$s) "
            + "jobs:(%1$s) background:(%1$s)^500 education:(%1$s) title:(%1$s) capabilities:(%1$s))";

    /**
     * The search text.
     */
    private String searchText = "foo bar";

    /**
     * PersonId to use.
     */
    private final long userPersonId = 38281L;

    /**
     * The organization short name to use.
     */
    private final String orgShortName = "heynow42";

    /**
     * The organization id for the above org short name.
     */
    private final String orgId = "958302";

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new DirectorySearchLuceneQueryBuilder(allEntitiesSearch, fieldBooster, fieldBoostFactor, orgIdGetter);
    }

    /**
     * Test buildNativeQuery with no weighted field, for all organizations, and for a user that's not logged in.
     */
    @Test
    public void testBuildNativeQueryNoWeightedFieldAllOrgsNoUser()
    {
        String expected = "+(lastName:(foo bar)^2 name:(foo bar) overview:(foo bar) biography:(foo bar) "
                + "jobs:(foo bar) background:(foo bar) education:(foo bar) title:(foo bar) capabilities:(foo bar))";

        assertEquals(expected, sut.buildNativeQuery(searchText, "", "", 0));

        context.assertIsSatisfied();
    }

    /**
     * Test buildNativeQuery with no weighted field, for all organizations, and for a user that is logged in.
     */
    @Test
    public void testBuildNativeQueryNoWeightedFieldAllOrgsWithUser()
    {
        String expected = "+(lastName:(foo bar)^2 name:(foo bar) overview:(foo bar) biography:(foo bar) "
                + "jobs:(foo bar) background:(foo bar) education:(foo bar) title:(foo bar) capabilities:(foo bar))";

        assertEquals(expected, sut.buildNativeQuery(searchText, "", "", userPersonId));
        context.assertIsSatisfied();
    }

    /**
     * Test buildNativeQuery with no weighted field, for a specific org, and for a user that's not logged in.
     */
    @Test
    public void testBuildNativeQueryNoWeightedFieldSpecificOrgNoUser()
    {
        context.checking(new Expectations()
        {
            {
                one(orgIdGetter).getIdentifier(orgShortName);
                will(returnValue(orgId));
            }
        });

        String expected = "+(+(lastName:(foo bar)^2 name:(foo bar) overview:(foo bar) biography:(foo bar) "
                + "jobs:(foo bar) background:(foo bar) education:(foo bar) title:(foo bar) capabilities:(foo bar))) "
                + "+parentOrganizationIdHierarchy:(" + orgId + ")";

        assertEquals(expected, sut.buildNativeQuery(searchText, "", orgShortName, 0));

        context.assertIsSatisfied();
    }

    /**
     * Test buildNativeQuery with no weighted field, for a specific org and logged-in user.
     */
    @Test
    public void testBuildNativeQueryNoWeightedFieldSpecificOrgWithUser()
    {
        context.checking(new Expectations()
        {
            {
                one(orgIdGetter).getIdentifier(orgShortName);
                will(returnValue(orgId));
            }
        });

        String expected = "+(+(lastName:(foo bar)^2 name:(foo bar) overview:(foo bar) biography:(foo bar) "
                + "jobs:(foo bar) background:(foo bar) education:(foo bar) title:(foo bar) capabilities:(foo bar))) "
                + "+parentOrganizationIdHierarchy:(" + orgId + ")";

        assertEquals(expected, sut.buildNativeQuery(searchText, "", orgShortName, userPersonId));

        context.assertIsSatisfied();
    }

    /**
     * Test buildNativeQuery with a weighted field, for all organizations, and for a user that's not logged in.
     */
    @Test
    public void testBuildNativeQueryWithWeightedFieldAllOrgsNoUser()
    {
        context.checking(new Expectations()
        {
            {
                one(fieldBooster).boostField(allEntitiesSearch, "background", fieldBoostFactor);
                will(returnValue(boostedFieldQueryMask));
            }
        });

        String expected = "+(lastName:(foo bar)^2 name:(foo bar) overview:(foo bar) biography:(foo bar) "
                + "jobs:(foo bar) background:(foo bar)^500 education:(foo bar) title:(foo bar) capabilities:(foo bar))";

        assertEquals(expected, sut.buildNativeQuery(searchText, "background", "", 0));

        context.assertIsSatisfied();
    }

    /**
     * Test buildNativeQuery.
     */
    @Test
    public void testBuildNativeQueryWithWeightedFieldAllOrgsWithUser()
    {
        context.checking(new Expectations()
        {
            {
                one(fieldBooster).boostField(allEntitiesSearch, "background", fieldBoostFactor);
                will(returnValue(boostedFieldQueryMask));
            }
        });

        String expected = "+(lastName:(foo bar)^2 name:(foo bar) overview:(foo bar) biography:(foo bar) "
                + "jobs:(foo bar) background:(foo bar)^500 education:(foo bar) title:(foo bar) capabilities:(foo bar))";

        assertEquals(expected, sut.buildNativeQuery(searchText, "background", "", userPersonId));

        context.assertIsSatisfied();
    }

    /**
     * Test buildNativeQuery.
     */
    @Test
    public void testBuildNativeQueryWithWeightedFieldSpecificOrgNoUser()
    {
        context.checking(new Expectations()
        {
            {
                one(orgIdGetter).getIdentifier(orgShortName);
                will(returnValue(orgId));

                one(fieldBooster).boostField(allEntitiesSearch, "background", fieldBoostFactor);
                will(returnValue(boostedFieldQueryMask));
            }
        });

        String expected = "+(+(lastName:(foo bar)^2 name:(foo bar) overview:(foo bar) biography:(foo bar) "
                + "jobs:(foo bar) background:(foo bar)^500 education:(foo bar) title:(foo bar) "
                + "capabilities:(foo bar))) +parentOrganizationIdHierarchy:(" + orgId + ")";

        assertEquals(expected, sut.buildNativeQuery(searchText, "background", orgShortName, 0));

        context.assertIsSatisfied();
    }

    /**
     * Test buildNativeQuery.
     */
    @Test
    public void testBuildNativeQueryWithWeightedFieldSpecificOrgWithUser()
    {
        context.checking(new Expectations()
        {
            {
                one(orgIdGetter).getIdentifier(orgShortName);
                will(returnValue(orgId));

                one(fieldBooster).boostField(allEntitiesSearch, "background", fieldBoostFactor);
                will(returnValue(boostedFieldQueryMask));
            }
        });

        String expected = "+(+(lastName:(foo bar)^2 name:(foo bar) overview:(foo bar) biography:(foo bar) "
                + "jobs:(foo bar) background:(foo bar)^500 education:(foo bar) title:(foo bar) "
                + "capabilities:(foo bar))) +parentOrganizationIdHierarchy:(" + orgId + ")";

        assertEquals(expected, sut.buildNativeQuery(searchText, "background", orgShortName, userPersonId));

        context.assertIsSatisfied();
    }

    /**
     * Test buildNativeQuery with no query string mask - for just a parent org filter.
     */
    @Test
    public void testBuildNativeQueryWithEmptyQueryString()
    {
        sut = new DirectorySearchLuceneQueryBuilder("", fieldBooster, fieldBoostFactor, orgIdGetter);
        context.checking(new Expectations()
        {
            {
                one(orgIdGetter).getIdentifier(orgShortName);
                will(returnValue(orgId));
            }
        });

        String expected = "+parentOrganizationIdHierarchy:(" + orgId + ")";

        assertEquals(expected, sut.buildNativeQuery(searchText, "", orgShortName, userPersonId));

        context.assertIsSatisfied();
    }
}
