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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Background;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.cache.PopulateOrgChildWithSkeletonParentOrgsCacheMapper;
import org.eurekastreams.server.persistence.mappers.cache.PopulatePeopleWithSkeletonRelatedOrgsCacheMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetPersonExecution class.
 * 
 */
public class GetPersonExecutionTest
{
    /**
     * An arbitrary person id to use for testing.
     */
    private static final int PERSON_ID = 123;

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
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal} mock.
     */
    private Principal actionContextPrincipal = context.mock(Principal.class);

    /**
     * Mocked tab mapper object for test.
     */
    private PersonMapper mapper = context.mock(PersonMapper.class);

    /**
     * mocked tab for testing results.
     */
    private Person testPerson = context.mock(Person.class);

    /**
     * Mocked mapper to populate a person's parent organization with a skeleton org.
     */
    private PopulateOrgChildWithSkeletonParentOrgsCacheMapper peopleParentOrgSkeletonPopulatorMock = context
            .mock(PopulateOrgChildWithSkeletonParentOrgsCacheMapper.class);

    /**
     * System under test.
     */
    private GetPersonExecution sut = null;

    /**
     * Mapper to populate a person's related orgs with skeleton organizations from cache.
     */
    private PopulatePeopleWithSkeletonRelatedOrgsCacheMapper skeletonRelatedOrgsMapper = context
            .mock(PopulatePeopleWithSkeletonRelatedOrgsCacheMapper.class);

    /**
     * Mocked mapper for retrieving the banner id.
     */
    private GetBannerIdByParentOrganizationStrategy getBannerIdMapperMock = context
            .mock(GetBannerIdByParentOrganizationStrategy.class);

    /**
     * Setup the test.
     */
    @Before
    public final void setup()
    {
        sut = new GetPersonExecution(mapper, skeletonRelatedOrgsMapper, peopleParentOrgSkeletonPopulatorMock,
                getBannerIdMapperMock);
    }

    /**
     * Retrieve a person using account id.
     * 
     * @throws Exception
     *             should not occur
     */
    @Test
    public void performActionWithValidAcctIdParameter() throws Exception
    {
        List<BackgroundItem> bgItems = new ArrayList<BackgroundItem>();
        final Background bg = new Background(null);
        bg.setBackgroundItems(bgItems, BackgroundItemType.SKILL);
        final String username = "ntid1234";

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(username));

                oneOf(mapper).findByAccountId(username);
                will(returnValue(testPerson));

                allowing(testPerson).getBackground();
                will(returnValue(bg));

                oneOf(peopleParentOrgSkeletonPopulatorMock).populateParentOrgSkeleton(testPerson);

                oneOf(skeletonRelatedOrgsMapper).execute(testPerson);

                oneOf(testPerson).getParentOrgId();
                will(returnValue(1L));

                oneOf(getBannerIdMapperMock).getBannerId(1L, testPerson);
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public void performActionWithNullParameter() throws Exception
    {
        final String username = "ntid1234";

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(null));

                allowing(actionContext).getPrincipal();
                will(returnValue(actionContextPrincipal));

                allowing(actionContextPrincipal).getAccountId();
                will(returnValue(username));

                oneOf(mapper).findByAccountId(username);
                will(returnValue(testPerson));

                oneOf(testPerson).getTabs(TabGroupType.START);

                oneOf(peopleParentOrgSkeletonPopulatorMock).populateParentOrgSkeleton(testPerson);

                oneOf(skeletonRelatedOrgsMapper).execute(testPerson);

                oneOf(testPerson).getParentOrgId();
                will(returnValue(1L));

                oneOf(getBannerIdMapperMock).getBannerId(1L, testPerson);
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

}
