/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetOrgCoordinators;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GetOrganizationModelViewByShortNameExecution.
 * 
 */
@SuppressWarnings("unchecked")
public class GetOrganizationModelViewByShortNameExecutionTest
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
     * {@link GetOrganizationsByShortNames}.
     */
    private GetOrganizationsByShortNames mapper = context.mock(GetOrganizationsByShortNames.class);

    /**
     * {@link GetRootOrganizationIdAndShortName}.
     */
    private GetRootOrganizationIdAndShortName rootOrgNameMapper = context.mock(GetRootOrganizationIdAndShortName.class);
    /**
     * Get org leader ids for an org.
     */
    private DomainMapper<Long, Set<Long>> orgLeaderIdsMapper = context.mock(DomainMapper.class, "orgLeaderIdsMapper");

    /**
     * Get org coordinator ids for an org.
     */
    private GetOrgCoordinators orgCoordinatorIdsMapper = context.mock(GetOrgCoordinators.class);

    /**
     * Get PersonModelViews by id.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> personModelViewsByIdMapper = context.mock(
            DomainMapper.class, "personModelViewsByIdMapper");;

    /**
     * Mapper to retrieve the banner id if it is not directly configured.
     */
    private GetBannerIdByParentOrganizationStrategy getBannerIdStrategy = context
            .mock(GetBannerIdByParentOrganizationStrategy.class);

    /**
     * {@link OrganizationModelView}.
     */
    private OrganizationModelView orgModelView = context.mock(OrganizationModelView.class);

    /**
     * Org id.
     */
    private final Long orgId = 1L;

    /**
     * {@link ActionContext} mock.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * System under test.
     */
    private GetOrganizationModelViewByShortNameExecution sut = new GetOrganizationModelViewByShortNameExecution(mapper,
            rootOrgNameMapper, orgLeaderIdsMapper, orgCoordinatorIdsMapper, personModelViewsByIdMapper,
            getBannerIdStrategy);

    /**
     * Test.
     */
    @Test
    public void performActionSuccess()
    {
        final String orgShortname = "foo";
        final List<OrganizationModelView> results = new ArrayList<OrganizationModelView>(Arrays.asList(orgModelView));

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(orgShortname));

                oneOf(mapper).execute(Collections.singletonList(orgShortname));
                will(returnValue(results));

                allowing(orgModelView).getEntityId();
                will(returnValue(orgId));

                oneOf(orgLeaderIdsMapper).execute(orgId);
                will(returnValue(new HashSet<PersonModelView>()));

                oneOf(orgCoordinatorIdsMapper).execute(orgId);
                will(returnValue(new HashSet<PersonModelView>()));

                oneOf(personModelViewsByIdMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<PersonModelView>()));

                oneOf(orgModelView).setLeaders(new ArrayList<PersonModelView>());

                oneOf(orgModelView).setCoordinators(new ArrayList<PersonModelView>());

                oneOf(orgModelView).setBannerEntityId(orgId);

                oneOf(orgModelView).getBannerId();
                will(returnValue("foo"));

            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void performActionNullShortName()
    {
        final String orgShortname = null;
        final List<OrganizationModelView> results = new ArrayList<OrganizationModelView>(Arrays.asList(orgModelView));

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(orgShortname));

                oneOf(rootOrgNameMapper).getRootOrganizationShortName();
                will(returnValue("rosn"));

                oneOf(mapper).execute(Collections.singletonList("rosn"));
                will(returnValue(results));

                allowing(orgModelView).getEntityId();
                will(returnValue(orgId));

                oneOf(orgLeaderIdsMapper).execute(orgId);
                will(returnValue(new HashSet<PersonModelView>()));

                oneOf(orgCoordinatorIdsMapper).execute(orgId);
                will(returnValue(new HashSet<PersonModelView>()));

                oneOf(personModelViewsByIdMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<PersonModelView>()));

                oneOf(orgModelView).setLeaders(new ArrayList<PersonModelView>());

                oneOf(orgModelView).setCoordinators(new ArrayList<PersonModelView>());

                oneOf(orgModelView).setBannerEntityId(orgId);

                oneOf(orgModelView).getBannerId();
                will(returnValue("foo"));
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void performActionEmptyShortName()
    {
        final String orgShortname = "";
        final List<OrganizationModelView> results = new ArrayList<OrganizationModelView>(Arrays.asList(orgModelView));

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(orgShortname));

                oneOf(rootOrgNameMapper).getRootOrganizationShortName();
                will(returnValue("rosn"));

                oneOf(mapper).execute(Collections.singletonList("rosn"));
                will(returnValue(results));

                allowing(orgModelView).getEntityId();
                will(returnValue(orgId));

                oneOf(orgLeaderIdsMapper).execute(orgId);
                will(returnValue(new HashSet<PersonModelView>()));

                oneOf(orgCoordinatorIdsMapper).execute(orgId);
                will(returnValue(new HashSet<PersonModelView>()));

                oneOf(personModelViewsByIdMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<PersonModelView>()));

                oneOf(orgModelView).setLeaders(new ArrayList<PersonModelView>());

                oneOf(orgModelView).setCoordinators(new ArrayList<PersonModelView>());

                oneOf(orgModelView).setBannerEntityId(orgId);

                oneOf(orgModelView).getBannerId();
                will(returnValue("foo"));
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

}
