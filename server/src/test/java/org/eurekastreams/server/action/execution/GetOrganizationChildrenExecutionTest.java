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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.action.request.directory.GetDirectorySearchResultsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.ResourceSortCriteria;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.directory.OrgChildrenGetter;
import org.eurekastreams.server.service.actions.strategies.directory.TransientPropertyPopulator;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Test for GetOrganizationChildrenExecution.
 * 
 */
@SuppressWarnings("unchecked")
public class GetOrganizationChildrenExecutionTest
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
     * The root org getter to use.
     */
    private GetRootOrganizationIdAndShortName rootOrgGetter = context.mock(GetRootOrganizationIdAndShortName.class);

    /**
     * The org children getter to use.
     */
    private OrgChildrenGetter<PersonModelView> orgChildrenGetter = context.mock(OrgChildrenGetter.class);

    /**
     * The transient property populator mock.
     */
    private final TransientPropertyPopulator propertyPopulator = context.mock(TransientPropertyPopulator.class);

    /**
     * Mocked up sut for validation testing.
     */
    private GetOrganizationChildrenExecution sut = new GetOrganizationChildrenExecution<PersonModelView>(rootOrgGetter,
            orgChildrenGetter, propertyPopulator);

    /**
     * The mocked UserDetails.
     */
    private final UserDetails userDetails = context.mock(UserDetails.class);

    /**
     * The short name to use.
     */
    private static final String ORG_SHORT_NAME = "barfoo";

    /**
     * The starting index to use.
     */
    private static final int FROM = 0;

    /**
     * The ending index to use.
     */
    private static final int TO = 9;

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * The resource sort criteria to use.
     */
    private final ResourceSortCriteria resourceSortCriteria = context.mock(ResourceSortCriteria.class);

    /**
     * Test performAction().
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testPerformActionWithShortName() throws Exception
    {
        final GetDirectorySearchResultsRequest request = new GetDirectorySearchResultsRequest(ORG_SHORT_NAME, FROM, TO,
                resourceSortCriteria);

        final PagedSet<ModelView> results = new PagedSet<ModelView>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(0L));

                one(orgChildrenGetter).getOrgChildren(ORG_SHORT_NAME, FROM, TO, resourceSortCriteria, 0);
                will(returnValue(results));

                oneOf(propertyPopulator).populateTransientProperties(results.getPagedSet(), 0L, ORG_SHORT_NAME);
            }
        });

        assertSame(results, sut.execute(actionContext));

        context.assertIsSatisfied();
    }

    /**
     * Test performAction().
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testPerformActionWithShortNameLoggedIn() throws Exception
    {
        final GetDirectorySearchResultsRequest request = new GetDirectorySearchResultsRequest(ORG_SHORT_NAME, FROM, TO,
                resourceSortCriteria);
        final PagedSet<ModelView> results = new PagedSet<ModelView>();
        final ExtendedUserDetails user = context.mock(ExtendedUserDetails.class);
        final Person person = context.mock(Person.class);
        final long personId = 8873L;

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(personId));

                one(orgChildrenGetter).getOrgChildren(ORG_SHORT_NAME, FROM, TO, resourceSortCriteria, personId);
                will(returnValue(results));

                oneOf(propertyPopulator).populateTransientProperties(results.getPagedSet(), personId, ORG_SHORT_NAME);

            }
        });

        assertSame(results, sut.execute(actionContext));

        context.assertIsSatisfied();
    }

    /**
     * Test performAction() with null org shortname.
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testPerformActionWithNullShortName() throws Exception
    {
        final GetDirectorySearchResultsRequest request = new GetDirectorySearchResultsRequest(null, FROM, TO,
                resourceSortCriteria);
        final PagedSet<ModelView> results = new PagedSet<ModelView>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(0L));

                one(rootOrgGetter).getRootOrganizationShortName();
                will(returnValue(ORG_SHORT_NAME));

                one(orgChildrenGetter).getOrgChildren(ORG_SHORT_NAME, FROM, TO, resourceSortCriteria, 0);
                will(returnValue(results));

                oneOf(propertyPopulator).populateTransientProperties(results.getPagedSet(), 0L, ORG_SHORT_NAME);

            }
        });

        assertSame(results, sut.execute(actionContext));

        context.assertIsSatisfied();
    }

    /**
     * Test performAction() with empty org shortname.
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testPerformActionWithEmptyShortName() throws Exception
    {
        final GetDirectorySearchResultsRequest request = new GetDirectorySearchResultsRequest("", FROM, TO,
                resourceSortCriteria);
        final PagedSet<ModelView> results = new PagedSet<ModelView>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(0L));

                one(rootOrgGetter).getRootOrganizationShortName();
                will(returnValue(ORG_SHORT_NAME));

                one(orgChildrenGetter).getOrgChildren(ORG_SHORT_NAME, FROM, TO, resourceSortCriteria, 0);
                will(returnValue(results));

                oneOf(propertyPopulator).populateTransientProperties(results.getPagedSet(), 0L, ORG_SHORT_NAME);

            }
        });

        assertSame(results, sut.execute(actionContext));

        context.assertIsSatisfied();
    }

}
