/*
 * Copyright (c) 2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.action.execution.opensocial;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.opensocial.GetPeopleByOpenSocialIdsRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
* Test suite for the {@link GetPeopleByOpenSocialIdsExecution} class.
*
*/
public class GetPeopleByOpenSocialIdsExecutionTest
{
    /**
    * System under test.
    */
    private GetPeopleByOpenSocialIdsExecution sut;

    /**
    * Collection of people to be used a test of results for the perform action method.
    */
    private static List<PersonModelView> people = new LinkedList<PersonModelView>();

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
    * Mocked principal object for test.
    */
    private Principal principal = context.mock(Principal.class);

    /**
    * Mocked person mapper object for test.
    */
    private GetPeopleByOpenSocialIds getPersonModelViewsByOpenSocialIdsMapper = context
            .mock(GetPeopleByOpenSocialIds.class);

    /**
    * Mocked mapper object for retrieving a list of PersonModelView objects by account ids.
    */
    private GetPeopleByAccountIds getPersonModelViewsByAccountIdsMapper = context
            .mock(GetPeopleByAccountIds.class);

    /**
    * An Open Social id to use for testing. Arbitrary.
    */
    private static final String SUBJECT_OPENSOCIAL_ID = UUID.randomUUID().toString();

    /**
    * Another Open Social id. Arbitrary.
    */
    private static final String AUTHOR_OPENSOCIAL_ID = UUID.randomUUID().toString();

    /**
    * Prepare the sut.
    */
    @Before
    public void setup()
    {
        sut = new GetPeopleByOpenSocialIdsExecution(getPersonModelViewsByOpenSocialIdsMapper,
                getPersonModelViewsByAccountIdsMapper);
    }

    /**
    * This test covers the PerformAction method when type = self.
    *
    * @throws Exception
    * unexpected.
    */
    @Test
    public void testPerformActionWithTypeSelf() throws Exception
    {
        final LinkedList<String> openSocialIds = new LinkedList<String>();
        openSocialIds.add(SUBJECT_OPENSOCIAL_ID);
        openSocialIds.add(AUTHOR_OPENSOCIAL_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonModelViewsByOpenSocialIdsMapper).execute(openSocialIds);
                will(returnValue(people));
            }
        });

        GetPeopleByOpenSocialIdsRequest currentRequest = new GetPeopleByOpenSocialIdsRequest(openSocialIds, "self");

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principal);

        sut.execute(currentContext);

        context.assertIsSatisfied();
    }
}