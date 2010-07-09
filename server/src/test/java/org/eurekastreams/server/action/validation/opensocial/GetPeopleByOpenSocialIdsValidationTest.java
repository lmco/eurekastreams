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
package org.eurekastreams.server.action.validation.opensocial;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.opensocial.GetPeopleByOpenSocialIdsRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link GetPeopleByOpenSocialIdsValidation} class.
 *
 */
public class GetPeopleByOpenSocialIdsValidationTest
{
    /**
     * System under test.
     */
    private GetPeopleByOpenSocialIdsValidation sut;

    /**
     * Collection of people to be used a test of results for the perform action method.
     */
    private static List<Person> people = new LinkedList<Person>();

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
    private PersonMapper mapper = context.mock(PersonMapper.class);

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
        sut = new GetPeopleByOpenSocialIdsValidation();
    }

    /**
     * This test covers the validation method when type = self.
     *
     * @throws Exception
     *             unexpected.
     */
    @Test
    public void testValidationWithTypeSelf() throws Exception
    {
        final LinkedList<String> openSocialIds = new LinkedList<String>();
        openSocialIds.add(SUBJECT_OPENSOCIAL_ID);
        openSocialIds.add(AUTHOR_OPENSOCIAL_ID);

        GetPeopleByOpenSocialIdsRequest currentRequest = new GetPeopleByOpenSocialIdsRequest(openSocialIds, "self");

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principal);

        sut.validate(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * This test covers the Validation method when type = friends.
     *
     * @throws Exception
     *             unexpected.
     */
    @Test
    public void testValidationWithTypeFriends() throws Exception
    {
        final LinkedList<String> openSocialIds = new LinkedList<String>();
        openSocialIds.add(SUBJECT_OPENSOCIAL_ID);
        openSocialIds.add(AUTHOR_OPENSOCIAL_ID);

        GetPeopleByOpenSocialIdsRequest currentRequest = new GetPeopleByOpenSocialIdsRequest(openSocialIds, "friends");

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principal);

        sut.validate(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * This test covers the PerformAction method with illegal type.
     *
     * @throws Exception
     *             unexpected.
     */
    @Test(expected = ValidationException.class)
    public void testValidationFailureWithInvalidType() throws Exception
    {
        final LinkedList<String> openSocialIds = new LinkedList<String>();
        openSocialIds.add(SUBJECT_OPENSOCIAL_ID);
        openSocialIds.add(AUTHOR_OPENSOCIAL_ID);

        GetPeopleByOpenSocialIdsRequest currentRequest = new GetPeopleByOpenSocialIdsRequest(openSocialIds, "testing");

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principal);

        sut.validate(currentContext);

        context.assertIsSatisfied();
    }
}
