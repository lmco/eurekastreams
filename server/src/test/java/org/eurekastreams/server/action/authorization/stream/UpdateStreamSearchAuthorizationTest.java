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
package org.eurekastreams.server.action.authorization.stream;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.NoResultException;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.persistence.mappers.FindUserStreamSearchById;
import org.eurekastreams.server.persistence.mappers.requests.FindUserStreamFilterByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Class to test the {@link UpdateStreamSearchAuthorization} class.
 *
 */
public class UpdateStreamSearchAuthorizationTest
{
    /**
     * System under test.
     */
    private UpdateStreamSearchAuthorization sut;

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
     * FindUserStreamSearchById DAO.
     */
    private FindUserStreamSearchById userStreamSearchByIdDAO = context.mock(FindUserStreamSearchById.class);

    /**
     * Mocked principal object for this test suite.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Test fields to push into authorizer.
     */
    private HashMap<String, Serializable> fields;

    /**
     * Setup sut.
     */
    @Before
    public void setup()
    {
        sut = new UpdateStreamSearchAuthorization(userStreamSearchByIdDAO);
        fields = new HashMap<String, Serializable>();
        fields.put("id", 1L);
    }

    /**
     * Test the successful authorization path.
     */
    @Test
    public void testAuthorize()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(1L));

                oneOf(userStreamSearchByIdDAO).execute(with(any(FindUserStreamFilterByIdRequest.class)));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(fields, principalMock);

        sut.authorize(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Test the failure authorization path.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFailure()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(1L));

                oneOf(userStreamSearchByIdDAO).execute(with(any(FindUserStreamFilterByIdRequest.class)));
                will(throwException(new NoResultException()));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(fields, principalMock);

        sut.authorize(currentContext);
        context.assertIsSatisfied();
    }
}
