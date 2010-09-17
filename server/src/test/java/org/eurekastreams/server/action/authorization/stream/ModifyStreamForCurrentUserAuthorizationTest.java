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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.transformer.RequestTransformer;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Class for authorizer.
 *
 */
public class ModifyStreamForCurrentUserAuthorizationTest
{
    /**
     * System under test.
     */
    private ModifyStreamForCurrentUserAuthorization sut;

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
     * Action context.
     */
    private final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Person Mapper mock.
     */
    private FindByIdMapper<Person> personMapper = context.mock(FindByIdMapper.class);

    /**
     * Request transformer.
     */
    private RequestTransformer requestTransformer = context.mock(RequestTransformer.class);

    /**
     * Test principal.
     */
    private final Principal principal = context.mock(Principal.class);

    /**
     * Before.
     */
    @Before
    public void setup()
    {
        sut = new ModifyStreamForCurrentUserAuthorization(personMapper, requestTransformer);
    }

    /**
     * Test authorize method.
     *
     * @throws Exception
     *             on error.
     */
    @Test
    public void testAuthorizeSuccess() throws Exception
    {
        final HashMap<String, Serializable> state = new HashMap<String, Serializable>();
        final Person person = context.mock(Person.class);
        final List<Stream> streams = new ArrayList<Stream>();
        final Stream stream1 = new Stream();
        stream1.setId(1L);
        streams.add(stream1);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();

                allowing(actionContext).getState();
                will(returnValue(state));

                oneOf(personMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                oneOf(person).getStreams();
                will(returnValue(streams));

                oneOf(requestTransformer).transform(actionContext);
                will(returnValue(1L));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test authorize method.
     *
     * @throws Exception
     *             on error.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFail() throws Exception
    {
        final HashMap<String, Serializable> state = new HashMap<String, Serializable>();
        final Person person = context.mock(Person.class);
        final List<Stream> streams = new ArrayList<Stream>();
        final Stream stream1 = new Stream();
        stream1.setId(2L);
        streams.add(stream1);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();

                allowing(actionContext).getState();
                will(returnValue(state));

                oneOf(personMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                oneOf(person).getStreams();
                will(returnValue(streams));

                oneOf(requestTransformer).transform(actionContext);
                will(returnValue(1L));

                allowing(principal).getAccountId();
                will(returnValue("joebob"));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

}

