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
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamView.Type;
import org.eurekastreams.server.persistence.mappers.FindUserStreamViewById;
import org.eurekastreams.server.persistence.mappers.requests.FindUserStreamFilterByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link UpdateStreamViewAuthorization} class.
 *
 */
public class UpdateStreamViewAuthorizationTest
{
    /**
     * System under test.
     */
    private UpdateStreamViewAuthorization sut;

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
     * Mocked instance of the {@link FindUserStreamViewById} mapper.
     */
    private final FindUserStreamViewById findUserStreamViewByIdMock = context.mock(FindUserStreamViewById.class);

    /**
     * Mocked instance of the principal object.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked instance of the {@link StreamView} object.
     */
    private final StreamView streamViewMock = context.mock(StreamView.class);

    /**
     * Test fields to be used in test suite.
     */
    private HashMap<String, Serializable> fields;

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new UpdateStreamViewAuthorization(findUserStreamViewByIdMock);
        fields = new HashMap<String, Serializable>();
        fields.put("id", 1L);
    }

    /**
     * Test the successful path for authorization.
     */
    @Test
    public void testAuthorize()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(1L));

                oneOf(findUserStreamViewByIdMock).execute(with(any(FindUserStreamFilterByIdRequest.class)));
                will(returnValue(streamViewMock));

                oneOf(streamViewMock).getType();
                will(returnValue(null));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(fields, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the failure path for authorization.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFailBadType()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(1L));

                oneOf(findUserStreamViewByIdMock).execute(with(any(FindUserStreamFilterByIdRequest.class)));
                will(returnValue(streamViewMock));

                oneOf(streamViewMock).getType();
                will(returnValue(Type.EVERYONE));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(fields, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the failure path for authorization.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFailBadStreamView()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(1L));

                oneOf(findUserStreamViewByIdMock).execute(with(any(FindUserStreamFilterByIdRequest.class)));
                will(throwException(new NoResultException()));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(fields, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }
}
