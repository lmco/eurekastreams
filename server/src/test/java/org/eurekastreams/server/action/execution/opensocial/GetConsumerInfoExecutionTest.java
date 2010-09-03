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
package org.eurekastreams.server.action.execution.opensocial;

import net.oauth.OAuthServiceProvider;

import org.apache.shindig.auth.SecurityToken;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.GetConsumerInfoRequest;
import org.eurekastreams.server.action.response.opensocial.ConsumerInfoResponse;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthConsumerRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link GetConsumerInfoExecution}.
 */
@SuppressWarnings("unchecked")
public class GetConsumerInfoExecutionTest
{
    /**
     * System under test.
     */
    private GetConsumerInfoExecution sut;

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
     * Instance of OAuth consumer mapper injected by spring.
     */
    private final DomainMapper<OAuthConsumerRequest, OAuthConsumer> consumerMapper = context.mock(DomainMapper.class);

    /**
     * Mocked instance of the action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mocked instance of the security token.
     */
    private SecurityToken securityToken = context.mock(SecurityToken.class);

    /**
     * Mocked instance of the provider.
     */
    private OAuthServiceProvider provider = context.mock(OAuthServiceProvider.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new GetConsumerInfoExecution(consumerMapper);
    }

    /**
     * Test normal path through getConsumerKeyAndSecret.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test
    public void testGetConsumerKeyAndSecret() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(new GetConsumerInfoRequest(securityToken, "serviceName", provider)));

                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(consumerMapper).execute(with(any(OAuthConsumerRequest.class)));
                will(returnValue(new OAuthConsumer("", "", "", "", "")));
            }
        });
        ConsumerInfoResponse response = (ConsumerInfoResponse) sut.execute(actionContext);
        Assert.assertNotNull("Consumer was not found", response.getConsumerInfo());
        context.assertIsSatisfied();
    }

    /**
     * Test null consumer key with getConsumerKeyAndSecret.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = ExecutionException.class)
    public void testGetConsumerWithNullConsumerKey() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(new GetConsumerInfoRequest(securityToken, "serviceName", provider)));

                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(consumerMapper).execute(with(any(OAuthConsumerRequest.class)));
                will(returnValue(null));
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
