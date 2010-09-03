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

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.RemoveConsumerTokenRequest;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthToken;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthConsumerRequest;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthTokenRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link RemoveConsumerTokenExecution}.
 */
@SuppressWarnings("unchecked")
public class RemoveConsumerTokenExecutionTest
{
    /**
     * System under test.
     */
    private RemoveConsumerTokenExecution sut;

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
    private final DomainMapper<OAuthConsumerRequest, OAuthConsumer> consumerMapper = context.mock(DomainMapper.class,
            "consumerMock");

    /**
     * Instance of OAuth token mapper injected by spring.
     */
    private final DomainMapper<OAuthTokenRequest, Boolean> tokenDeleteMapper = context.mock(DomainMapper.class,
            "deleteMock");

    /**
     * Mocked instance of the action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mocked instance of the security token.
     */
    private SecurityToken securityToken = context.mock(SecurityToken.class);

    /**
     * Mocked instance of the consumer info.
     */
    private ConsumerInfo consumerInfo = context.mock(ConsumerInfo.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new RemoveConsumerTokenExecution(consumerMapper, tokenDeleteMapper);
    }

    /**
     * Test normal path through removeToken.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test
    public void testRemoveToken() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(new RemoveConsumerTokenRequest(securityToken, consumerInfo, "serviceName", // \n
                        "tokenName")));

                OAuthConsumer consumer = new OAuthConsumer("", "", "", "", "");
                OAuthToken token = new OAuthToken(consumer, "", "", "", "");
                token.setTokenExpireMillis(0L);

                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(securityToken).getViewerId();
                will(returnValue("123"));

                oneOf(securityToken).getOwnerId();
                will(returnValue("456"));

                oneOf(consumerMapper).execute(with(any(OAuthConsumerRequest.class)));
                will(returnValue(new OAuthConsumer("", "", "", "", "")));

                oneOf(tokenDeleteMapper).execute(with(any(OAuthTokenRequest.class)));
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test null consumer key with removeToken.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = ExecutionException.class)
    public void testRemoveTokenWithNullConsumerKey() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(new RemoveConsumerTokenRequest(securityToken, consumerInfo, "serviceName", // \n
                        "tokenName")));

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
