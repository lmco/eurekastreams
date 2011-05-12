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
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
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
            rootOrgNameMapper);

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

                oneOf(orgModelView).setBannerEntityId(orgId);

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

                oneOf(orgModelView).setBannerEntityId(orgId);

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

                oneOf(orgModelView).setBannerEntityId(orgId);

            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

}
