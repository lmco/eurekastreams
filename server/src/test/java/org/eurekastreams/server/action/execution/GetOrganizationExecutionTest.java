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

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.service.actions.strategies.OrganizationLoader;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetOrganizationExecution class.
 *
 */
public class GetOrganizationExecutionTest
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
     * Subject under test.
     */
    private GetOrganizationExecution sut;

    /**
     * The mock mapper to be used by the action.
     */
    private OrganizationMapper orgMapperMock = context.mock(OrganizationMapper.class);

    /**
     * {@link ActionContext} mock.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link OrganizationLoader}.
     */
    private OrganizationLoader orgLoader = context.mock(OrganizationLoader.class);

    /**
     * Mocked mapper for retrieving the banner id.
     */
    private GetBannerIdByParentOrganizationStrategy getBannerIdMapperMock =
        context.mock(GetBannerIdByParentOrganizationStrategy.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new GetOrganizationExecution(orgMapperMock, getBannerIdMapperMock);
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionSuccess() throws Exception
    {
        final String orgShortName = "existingOrg";
        final Organization orgMock = context.mock(Organization.class, "Tony");

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(orgShortName));

                oneOf(orgMapperMock).findByShortName(orgShortName);
                will(returnValue(orgMock));

                allowing(orgMock).getCapabilities();
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithLoaderSuccess() throws Exception
    {
        final String orgShortName = "existingOrg";
        final Organization orgMock = context.mock(Organization.class, "Tony");

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(orgShortName));

                oneOf(orgMapperMock).findByShortName(orgShortName);
                will(returnValue(orgMock));

                allowing(orgMock).getCapabilities();

                allowing(orgLoader).load(orgMock);

                oneOf(orgMock).getId();

                oneOf(orgMock).setBannerEntityId(with(any(Long.class)));

                oneOf(orgMock).getBannerId();
                will(returnValue(null));

                oneOf(orgMock).getParentOrgId();
                will(returnValue(1L));

                oneOf(getBannerIdMapperMock).getBannerId(1L, orgMock);
            }
        });

        sut.setOrganizationLoader(orgLoader);
        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithNoOrgName() throws Exception
    {
        final String orgShortName = "";
        final Organization orgMock = context.mock(Organization.class, "Tony");

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(orgShortName));

                allowing(orgMock).getShortName();
                will(returnValue("Tony"));

                oneOf(orgMapperMock).getRootOrganization();
                will(returnValue(orgMock));

                allowing(orgMock).getCapabilities();

                allowing(orgLoader).load(orgMock);

                oneOf(orgMock).getId();

                oneOf(orgMock).setBannerEntityId(with(any(Long.class)));

                oneOf(orgMock).getBannerId();
                will(returnValue(null));

                oneOf(orgMock).getParentOrgId();
                will(returnValue(1L));

                oneOf(getBannerIdMapperMock).getBannerId(1L, orgMock);
            }
        });

        sut.setOrganizationLoader(orgLoader);
        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

}
