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

import net.oauth.OAuthConsumer;
import net.oauth.OAuthProblemException;

import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.principal.PrincipalPopulatorTransWrapper;
import org.eurekastreams.server.action.response.opensocial.SecurityTokenResponse;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

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
     * The mock {@link ServiceActionController}.
     */
    private ServiceActionController serviceActionControllerMock = context.mock(ServiceActionController.class);

    /**
     * The mock {@link PrincipalPopulatorTransWrapper}.
     */
    private PrincipalPopulatorTransWrapper principalPopulator = context.mock(PrincipalPopulatorTransWrapper.class);

    /**
     * Instance of the CreateOauthRequestToken Service Action.
     */
    private ServiceAction createOAuthRequestTokenAction = context.mock(ServiceAction.class,
            "createOAuthRequestTokenAction");

    /**
     * Instance of the AuthorizeOAuthToken Service Action.
     */
    private ServiceAction authorizeOAuthTokenAction = context.mock(ServiceAction.class, "authorizeOAuthTokenAction");

    /**
     * Instance of the UpdateRequestToAccessToken Service Action.
     */
    private ServiceAction updateRequestToAccessTokenAction = context.mock(ServiceAction.class,
            "updateRequestToAccessTokenAction");

    /**
     * Instance of the GetOAuthEntryByToken Service Action.
     */
    private ServiceAction getOAuthEntryByTokenAction = context.mock(ServiceAction.class, "getOAuthEntryByTokenAction");

    /**
     * Instance of the DisableOAuthToken Service Action.
     */
    private ServiceAction disableOAuthTokenAction = context.mock(ServiceAction.class, "disableOAuthTokenAction");

    /**
     * Instance of the RemoveOAuthToken Service Action.
     */
    private ServiceAction removeOAuthTokenAction = context.mock(ServiceAction.class, "removeOAuthTokenAction");

    /**
     * Mocked instnace of the ServiceActionContext.
     */
    private ServiceActionContext serviceActionContextMock = context.mock(ServiceActionContext.class);

    /**
     * Instance of the RemoveOAuthToken Service Action.
     */
    private ServiceAction getOAuthConsumerByConsumerKeyAction = context.mock(ServiceAction.class,
            "getOAuthConsumerByConsumerKeyAction");

    /**
     * Instance of the GetSecurity Tocken for Consumer Request Service Action.
     */
    private ServiceAction getSecurityTokenForConsumerRequestAction = context.mock(ServiceAction.class,
            "getSecurityTokenForConsumerRequestAction");

    /**
     * Set up the system to test.
     */
    @Before
    public void setUp()
    {
        sut = new OAuthDataStoreImpl(createOAuthRequestTokenAction, createOAuthRequestTokenAction,
                createOAuthRequestTokenAction, getOAuthEntryByTokenAction, disableOAuthTokenAction,
                removeOAuthTokenAction, getOAuthConsumerByConsumerKeyAction, getSecurityTokenForConsumerRequestAction);
        sut.setPrincipalPopulatorTransWrapper(principalPopulator);
        sut.setServiceActionController(serviceActionControllerMock);
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
        final OAuthEntry entry = new OAuthEntry();
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(entry));
            }
        });

        sut.generateRequestToken("key", "1.0", "http://localhost:8080/gadgets/oauthcallback");
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
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
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
                oneOf(principalPopulator).getPrincipal(with(any(String.class)), with(any(String.class)));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
            }
        });
        sut.authorizeToken(testEntry, TEST_ARG1);
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
                oneOf(principalPopulator).getPrincipal(with(any(String.class)), with(any(String.class)));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
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
        final OAuthEntry entry = new OAuthEntry();
        context.checking(new Expectations()
        {
            {
                oneOf(principalPopulator).getPrincipal(with(any(String.class)), with(any(String.class)));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(entry));
            }
        });

        sut.convertToAccessToken(testEntry);
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
                oneOf(principalPopulator).getPrincipal(with(any(String.class)), with(any(String.class)));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
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
        final OAuthConsumer consumer = new OAuthConsumer(null, null, null, null);
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(consumer));
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
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
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
        final OAuthEntry entry = new OAuthEntry();
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(entry));
            }
        });
        sut.getEntry(TEST_ARG1);
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
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });
        sut.getEntry(TEST_ARG1);
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
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
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
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
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
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
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
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
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
        final SecurityTokenResponse response = new SecurityTokenResponse(null);
        context.checking(new Expectations()
        {
            {
                oneOf(principalPopulator).getPrincipal(with(TEST_ARG1), with(any(String.class)));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(response));
            }
        });
        sut.getSecurityTokenForConsumerRequest(TEST_ARG1, TEST_ARG1);
        context.assertIsSatisfied();
    }

    /**
     * Test of failing GetSecurityTokenForConsumerRequest.
     * 
     * @throws Exception
     *             on error.
     */
    @Test(expected = OAuthProblemException.class)
    public void testGetSecurityTokenForConsumerRequestFailure() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalPopulator).getPrincipal(with(TEST_ARG1), with(any(String.class)));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });
        sut.getSecurityTokenForConsumerRequest(TEST_ARG1, TEST_ARG1);
        context.assertIsSatisfied();
    }
}
