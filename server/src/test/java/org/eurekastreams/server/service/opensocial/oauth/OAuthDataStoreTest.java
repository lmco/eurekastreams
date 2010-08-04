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
package org.eurekastreams.server.service.opensocial.oauth;

import static junit.framework.Assert.assertNotNull;
import net.oauth.OAuthProblemException;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.OAuthConsumerMapper;
import org.eurekastreams.server.persistence.OAuthEntryMapper;

/**
 * This class is a test stub for the OAuth implementation of the OpenSocial Gadget Container.
 * 
 */
public class OAuthDataStoreTest
{
    /**
     * The system under test.
     */
    private OAuthDataStoreImpl sut;

    /**
     * Test instance of OAuthEntry.
     */
    private OAuthDomainEntry testEntryDto = new OAuthDomainEntry();

    /**
     * Test instance of OAuthEntry.
     */
    private OAuthEntry testEntry = new OAuthEntry();

    /**
     * Test generic string for stubbing.
     */
    private static final String TEST_ARG1 = "testarg";

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
     * The mock entry mapper.
     */
    private OAuthEntryMapper entryMapper = context.mock(OAuthEntryMapper.class);

    /**
     * The mock transaction manager.
     */
    private PlatformTransactionManager transactionManager = context.mock(PlatformTransactionManager.class);

    /**
     * Set up the system to test.
     */
    @Before
    public void setUp()
    {
        sut = new OAuthDataStoreImpl("http://localhost:8080/resources/requesttoken", 
        		"http://localhost:8080/resources/authorize", 
        		"http://localhost:8080/resources/accesstoken", entryMapper, consumerMapper, transactionManager);
    }

    /**
     * Test generating a new request token.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testGenerateRequestToken() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                oneOf(consumerMapper).findConsumerByConsumerKey(with(any(String.class)));
                will(returnValue(new org.eurekastreams.server.domain.OAuthConsumer("", "", "", "", "")));

                oneOf(entryMapper).insert(with(any(OAuthDomainEntry.class)));
                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
            }
        });

        OAuthEntry actual = sut.generateRequestToken("key", "1.0", "http://localhost:8080/gadgets/oauthcallback");
        assertNotNull("OAuthEntry was not found", actual);
        context.assertIsSatisfied();
    }

    /**
     * Test generating a new request token where a rollback is necessary.
     * 
     * @throws Exception
     *             on error.
     */
    @Test(expected = OAuthProblemException.class)
    public void testGenerateRequestTokenWithRollback() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                oneOf(consumerMapper).findConsumerByConsumerKey(with(any(String.class)));
                will(returnValue(new org.eurekastreams.server.domain.OAuthConsumer("", "", "", "", "")));

                oneOf(entryMapper).insert(with(any(OAuthDomainEntry.class)));
                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
                will(throwException(new Exception()));

                oneOf(transactionManager).rollback(with(any(TransactionStatus.class)));
            }
        });

        sut.generateRequestToken("key", "1.0", "http://localhost:8080/gadgets/oauthcallback");
        context.assertIsSatisfied();
    }

    /**
     * Test of AuthorizeToken.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testAuthorizeToken() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                oneOf(entryMapper).findEntry(with(any(String.class)));
                will(returnValue(testEntryDto));

                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
            }
        });
        sut.authorizeToken(testEntry, TEST_ARG1);
        assertNotNull(testEntry);
        context.assertIsSatisfied();
    }

    /**
     * Test of AuthorizeToken where a rollback is necessary.
     * 
     * @throws Exception
     *             on error.
     */
    @Test(expected = OAuthProblemException.class)
    public void testAuthorizeTokenWithRollback() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                oneOf(entryMapper).findEntry(with(any(String.class)));
                will(returnValue(testEntry));

                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
                will(throwException(new Exception()));

                oneOf(transactionManager).rollback(with(any(TransactionStatus.class)));
            }
        });
        sut.authorizeToken(testEntry, TEST_ARG1);
        context.assertIsSatisfied();
    }

    /**
     * Test of ConvertToAccessToken.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testConvertToAccessToken() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                oneOf(consumerMapper).findConsumerByConsumerKey(with(any(String.class)));
                will(returnValue(new org.eurekastreams.server.domain.OAuthConsumer("", "", "", "", "")));

                oneOf(entryMapper).delete(with(any(String.class)));
                oneOf(entryMapper).insert(with(any(OAuthDomainEntry.class)));
                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
            }
        });

        OAuthEntry actual = sut.convertToAccessToken(testEntry);
        Assert.assertTrue(actual.type == OAuthEntry.Type.ACCESS);
        context.assertIsSatisfied();
    }

    /**
     * Test of ConvertToAccessToken where a rollback is necessary.
     * 
     * @throws Exception
     *             on error.
     */
    @Test(expected = OAuthProblemException.class)
    public void testConvertToAccessTokenWithRollback() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                oneOf(consumerMapper).findConsumerByConsumerKey(with(any(String.class)));
                will(returnValue(new org.eurekastreams.server.domain.OAuthConsumer("", "", "", "", "")));

                oneOf(entryMapper).delete(with(any(String.class)));
                oneOf(entryMapper).insert(with(any(OAuthDomainEntry.class)));
                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
                will(throwException(new Exception()));
                oneOf(transactionManager).rollback(with(any(TransactionStatus.class)));
            }
        });

        sut.convertToAccessToken(testEntry);
        context.assertIsSatisfied();
    }

    /**
     * Test of GetConsumer.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testGetConsumer() throws Exception
    {
        final org.eurekastreams.server.domain.OAuthConsumer testConsumer = 
            new org.eurekastreams.server.domain.OAuthConsumer("", "", "", "", "");
        context.checking(new Expectations()
        {
            {
                oneOf(consumerMapper).findConsumerByConsumerKey(with(any(String.class)));
                will(returnValue(testConsumer));
            }
        });
        
        sut.getConsumer(TEST_ARG1);
        context.assertIsSatisfied();
    }

    /**
     * Test of GetConsumer.
     * 
     * @throws Exception
     *             on error.
     */
    @Test(expected = OAuthProblemException.class)
    public void testGetConsumerFailure() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(consumerMapper).findConsumerByConsumerKey(with(any(String.class)));
                will(throwException(new Exception()));
            }
        });
        
        sut.getConsumer(TEST_ARG1);
        context.assertIsSatisfied();
    }
    /**
     * Test of GetEntry.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testGetEntry() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                testEntryDto.setConsumer(new org.eurekastreams.server.domain.OAuthConsumer("", "", "", "", ""));
                testEntryDto.setType("REQUEST");

                oneOf(entryMapper).findEntry(with(any(String.class)));
                will(returnValue(testEntryDto));
            }
        });
        OAuthEntry actual = sut.getEntry(TEST_ARG1);
        Assert.assertTrue(actual != null);
        context.assertIsSatisfied();
    }

    /**
     * Test of GetEntry where a rollback is necessary.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testGetEntryWithNullEntryDto() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(entryMapper).findEntry(with(any(String.class)));
                will(returnValue(null));
            }
        });
        OAuthEntry actual = sut.getEntry(TEST_ARG1);
        Assert.assertTrue(actual == null);
        context.assertIsSatisfied();
    }

    /**
     * Test of DisableToken.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testDisableToken() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                oneOf(entryMapper).findEntry(with(any(String.class)));
                will(returnValue(testEntryDto));

                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
            }
        });

        sut.disableToken(testEntry);
        context.assertIsSatisfied();
    }

    /**
     * Test of DisableToken where a rollback is necessary.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testDisableTokenWithRollback() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                oneOf(entryMapper).findEntry(with(any(String.class)));
                will(returnValue(testEntryDto));

                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
                will(throwException(new Exception()));
                oneOf(transactionManager).rollback(with(any(TransactionStatus.class)));
            }
        });

        sut.disableToken(testEntry);
        context.assertIsSatisfied();
    }

    /**
     * Test of Remove Token where a rollback is necessary.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testRemoveTokenWithRollback() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));
                oneOf(entryMapper).delete(with(any(String.class)));
                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
            }
        });

        sut.removeToken(testEntry);
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
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));
                oneOf(entryMapper).delete(with(any(String.class)));
                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
                will(throwException(new Exception()));
                oneOf(transactionManager).rollback(with(any(TransactionStatus.class)));
            }
        });

        sut.removeToken(testEntry);
        context.assertIsSatisfied();
    }

    /**
     * Test of GetSecurityTokenForConsumerRequest.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testGetSecurityTokenForConsumerRequest() throws Exception
    {
        SecurityToken actual = sut.getSecurityTokenForConsumerRequest(TEST_ARG1, TEST_ARG1);
        Assert.assertTrue(actual != null);
    }
}
