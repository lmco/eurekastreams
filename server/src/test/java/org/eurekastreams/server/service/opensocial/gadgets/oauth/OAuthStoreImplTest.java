/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import static junit.framework.Assert.assertNotNull;
import junit.framework.Assert;
import net.oauth.OAuthServiceProvider;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthToken;
import org.eurekastreams.server.persistence.OAuthConsumerMapper;
import org.eurekastreams.server.persistence.OAuthTokenMapper;

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
     * The mock consumer mapper.
     */
    private OAuthConsumerMapper consumerMapper = context.mock(OAuthConsumerMapper.class);

    /**
     * The mock token mapper.
     */
    private OAuthTokenMapper tokenMapper = context.mock(OAuthTokenMapper.class);

    /**
     * The mock transaction manager.
     */
    private PlatformTransactionManager transactionManager = context.mock(PlatformTransactionManager.class);

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
     * Prepare the test.
     */
    @Before
    public void setUp()
    {
        sut = new OAuthStoreImpl(consumerMapper, tokenMapper, transactionManager);
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
                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(consumerMapper).findConsumerByServiceNameAndGadgetUrl(with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(new OAuthConsumer("", "", "", "", "")));
            }
        });

        ConsumerInfo info = sut.getConsumerKeyAndSecret(securityToken, "myService", provider);
        assertNotNull("Consumer was not found", info);
        context.assertIsSatisfied();
    }

    /**
     * Test null consumer key with getConsumerKeyAndSecret.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = GadgetException.class)
    public void testGetConsumerWithNullConsumerKey() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(consumerMapper).findConsumerByServiceNameAndGadgetUrl(with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(null));
            }
        });

        sut.getConsumerKeyAndSecret(securityToken, "myService", provider);
        context.assertIsSatisfied();
    }

    /**
     * Test getTokenInfo that returns a null token.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test
    public void testGetNullTokenInfo() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(securityToken).getViewerId();
                will(returnValue("123"));

                oneOf(securityToken).getOwnerId();
                will(returnValue("456"));

                oneOf(consumerMapper).findConsumerByServiceNameAndGadgetUrl(with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(new OAuthConsumer("", "", "", "", "")));

                oneOf(tokenMapper).findToken(with(any(OAuthConsumer.class)), with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(null));
            }
        });

        TokenInfo info = sut.getTokenInfo(securityToken, consumerInfo, "myService", "myToken");
        Assert.assertTrue("Token was found", info == null);
        context.assertIsSatisfied();
    }

    /**
     * Test normal path through getTokenInfo.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test
    public void testGetTokenInfo() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                OAuthConsumer consumer = new OAuthConsumer("", "", "", "", "");
                OAuthToken token = new OAuthToken(consumer, "", "", "", "");
                token.setTokenExpireMillis(0L);

                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(securityToken).getViewerId();
                will(returnValue("123"));

                oneOf(securityToken).getOwnerId();
                will(returnValue("456"));

                oneOf(consumerMapper).findConsumerByServiceNameAndGadgetUrl(with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(new OAuthConsumer("", "", "", "", "")));

                oneOf(tokenMapper).findToken(with(any(OAuthConsumer.class)), with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(token));
            }
        });

        TokenInfo info = sut.getTokenInfo(securityToken, consumerInfo, "myService", "myToken");
        assertNotNull("Token was not found", info);
        context.assertIsSatisfied();
    }

    /**
     * Test null consumer key with getTokenInfo.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = GadgetException.class)
    public void testGetTokenWithNullConsumerKey() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(consumerMapper).findConsumerByServiceNameAndGadgetUrl(with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(null));
            }
        });

        sut.getTokenInfo(securityToken, consumerInfo, "myService", "myToken");
        context.assertIsSatisfied();
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
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));
                
                OAuthConsumer consumer = new OAuthConsumer("", "", "", "", "");
                OAuthToken token = new OAuthToken(consumer, "", "", "", "");
                token.setTokenExpireMillis(0L);

                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(securityToken).getViewerId();
                will(returnValue("123"));

                oneOf(securityToken).getOwnerId();
                will(returnValue("456"));

                oneOf(consumerMapper).findConsumerByServiceNameAndGadgetUrl(with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(new OAuthConsumer("", "", "", "", "")));

                oneOf(tokenMapper).delete(with(any(OAuthConsumer.class)), with(any(String.class)),
                        with(any(String.class)));
                
                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
            }
        });

        sut.removeToken(securityToken, consumerInfo, "myService", "myToken");
        context.assertIsSatisfied();
    }

    /**
     * Test null consumer key with removeToken.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = GadgetException.class)
    public void testRemoveTokenWithNullConsumerKey() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));
                
                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(consumerMapper).findConsumerByServiceNameAndGadgetUrl(with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(null));
            }
        });

        sut.removeToken(securityToken, consumerInfo, "myService", "myToken");
        context.assertIsSatisfied();
    }

    /**
     * Test normal path through setToken.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test
    public void testSetToken() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(consumerMapper).findConsumerByServiceNameAndGadgetUrl(with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(new OAuthConsumer("", "", "", "", "")));

                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                OAuthConsumer consumer = new OAuthConsumer("", "", "", "", "");
                OAuthToken token = new OAuthToken(consumer, "", "", "", "");
                token.setTokenExpireMillis(0L);

                oneOf(securityToken).getViewerId();
                will(returnValue("123"));

                oneOf(securityToken).getOwnerId();
                will(returnValue("456"));

                oneOf(tokenInfo).getAccessToken();
                will(returnValue("accesstoken"));

                oneOf(tokenInfo).getTokenSecret();
                will(returnValue("accesssecret"));

                oneOf(tokenInfo).getTokenExpireMillis();
                will(returnValue(0L));

                oneOf(tokenMapper).insert(with(any(OAuthToken.class)));
                
                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
            }
        });

        sut.setTokenInfo(securityToken, consumerInfo, "myService", "myToken", tokenInfo);
        context.assertIsSatisfied();
    }

    /**
     * Test setToken where a rollback is necessary.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = GadgetException.class)
    public void testSetTokenWithRollback() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(consumerMapper).findConsumerByServiceNameAndGadgetUrl(with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(new OAuthConsumer("", "", "", "", "")));

                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                OAuthConsumer consumer = new OAuthConsumer("", "", "", "", "");
                OAuthToken token = new OAuthToken(consumer, "", "", "", "");
                token.setTokenExpireMillis(0L);

                oneOf(securityToken).getViewerId();
                will(returnValue("123"));

                oneOf(securityToken).getOwnerId();
                will(returnValue("456"));

                oneOf(tokenInfo).getAccessToken();
                will(returnValue("accesstoken"));

                oneOf(tokenInfo).getTokenSecret();
                will(returnValue("accesssecret"));

                oneOf(tokenInfo).getTokenExpireMillis();
                will(returnValue(0L));

                oneOf(tokenMapper).insert(with(any(OAuthToken.class)));

                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
                will(throwException(new Exception()));

                oneOf(transactionManager).rollback(with(any(TransactionStatus.class)));
            }
        });

        sut.setTokenInfo(securityToken, consumerInfo, "myService", "myToken", tokenInfo);
        context.assertIsSatisfied();
    }

    /**
     * Test null consumer key with setToken.
     * 
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = GadgetException.class)
    public void testSetTokenWithNullConsumerKey() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));
                
                oneOf(securityToken).getAppUrl();
                will(returnValue("http://localhost:4040/some/path"));

                oneOf(consumerMapper).findConsumerByServiceNameAndGadgetUrl(with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(null));
            }
        });

        sut.setTokenInfo(securityToken, consumerInfo, "myService", "myToken", tokenInfo);
        context.assertIsSatisfied();
    }
}
