/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.opensocial.gadgets.oauth;

import net.oauth.OAuthServiceProvider;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.response.opensocial.ConsumerInfoResponse;
import org.eurekastreams.server.action.response.opensocial.TokenInfoResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class performs the test for the implementation of the Shindig OAuthStore interface.
 */
public class OAuthStoreImplTest
{
    /**
     * Object that is being tested.
     */
    private OAuthStoreImpl sut;

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
     * The mock {@link ServiceActionController}.
     */
    private ServiceActionController serviceActionControllerMock = context.mock(ServiceActionController.class);

    /**
     * The mock security token.
     */
    private SecurityToken securityToken = context.mock(SecurityToken.class);

    /**
     * The mock service provider.
     */
    private OAuthServiceProvider provider = context.mock(OAuthServiceProvider.class);

    /**
     * The mock consumer info.
     */
    private ConsumerInfo consumerInfo = context.mock(ConsumerInfo.class);

    /**
     * The mock token info.
     */
    private TokenInfo tokenInfo = context.mock(TokenInfo.class);

    /**
     * Instance of the GetConsumerInfo Service Action.
     */
    private ServiceAction getConsumerInfoAction = context.mock(ServiceAction.class, "getConsumerInfoAction");

    /**
     * Instance of the SetConsumerTokenInfo Service Action.
     */
    private ServiceAction setConsumerTokenInfoAction = context.mock(ServiceAction.class, "setConsumerTokenInfoAction");

    /**
     * Instance of the GetConsumerTokenInfo Service Action.
     */
    private ServiceAction getConsumerTokenInfoAction = context.mock(ServiceAction.class, "getConsumerTokenInfoAction");

    /**
     * Instance of the RemoveConsumerToken Service Action.
     */
    private ServiceAction removeConsumerTokenAction = context.mock(ServiceAction.class, "removeConsumerTokenAction");

    /**
     * Prepare the test.
     */
    @Before
    public void setUp()
    {
        sut = new OAuthStoreImpl(getConsumerInfoAction, setConsumerTokenInfoAction, getConsumerTokenInfoAction,
                removeConsumerTokenAction);
        sut.setServiceActionController(serviceActionControllerMock);
    }

    /**
     * Test of get consumer info.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testGetConsumerInfo() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(new ConsumerInfoResponse(new ConsumerInfo(null, null, null))));
            }
        });

        sut.getConsumerKeyAndSecret(securityToken, "serviceName", provider);
        context.assertIsSatisfied();
    }

    /**
     * Test of get consumer info where a rollback is necessary.
     * 
     * @throws Exception
     *             on error.
     */
    @Test(expected = GadgetException.class)
    public void testGetConsumerInfoWithRollback() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });

        sut.getConsumerKeyAndSecret(securityToken, "serviceName", provider);
        context.assertIsSatisfied();
    }

    /**
     * Test of set token info.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testSetTokenInfo() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
            }
        });

        sut.setTokenInfo(securityToken, consumerInfo, "serviceName", "tokenName", tokenInfo);
        context.assertIsSatisfied();
    }

    /**
     * Test of set token info where a rollback is necessary.
     * 
     * @throws Exception
     *             on error.
     */
    @Test(expected = GadgetException.class)
    public void testSetTokenInfoWithRollback() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });

        sut.setTokenInfo(securityToken, consumerInfo, "serviceName", "tokenName", tokenInfo);
        context.assertIsSatisfied();
    }

    /**
     * Test of get token info.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testGetTokenInfo() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(new TokenInfoResponse(new TokenInfo(null, null, null, 0))));
            }
        });

        sut.getTokenInfo(securityToken, consumerInfo, "serviceName", "tokenName");
        context.assertIsSatisfied();
    }

    /**
     * Test of get token info where a rollback is necessary.
     * 
     * @throws Exception
     *             on error.
     */
    @Test(expected = GadgetException.class)
    public void testGetTokenInfoWithRollback() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });

        sut.getTokenInfo(securityToken, consumerInfo, "serviceName", "tokenName");
        context.assertIsSatisfied();
    }

    /**
     * Test of get token info with a null response from service call.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testGetTokenInfoWithNullResponse() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(null));
            }
        });

        sut.getTokenInfo(securityToken, consumerInfo, "serviceName", "tokenName");
        context.assertIsSatisfied();
    }

    /**
     * Test of Remove Token.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testRemoveToken() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
            }
        });

        sut.removeToken(securityToken, consumerInfo, "serviceName", "tokenName");
        context.assertIsSatisfied();
    }

    /**
     * Test of Remove Token where a rollback is necessary.
     * 
     * @throws Exception
     *             on error.
     */
    @Test(expected = GadgetException.class)
    public void testRemoveTokenWithRollback() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });

        sut.removeToken(securityToken, consumerInfo, "serviceName", "tokenName");
        context.assertIsSatisfied();
    }
}
