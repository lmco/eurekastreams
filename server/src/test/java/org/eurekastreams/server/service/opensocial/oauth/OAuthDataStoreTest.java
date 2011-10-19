/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.response.opensocial.SecurityTokenResponse;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
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
    private final OAuthDomainEntry testEntryDto = new OAuthDomainEntry();

    /**
     * Test instance of OAuthEntry.
     */
    private final OAuthEntry testEntry = new OAuthEntry();

    /**
     * Test generic string for stubbing.
     */
    private static final String TEST_ARG1 = "testarg";

    /**
     * Context for building mock objects.
     */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * The mock {@link ServiceActionController}.
     */
    private final ServiceActionController serviceActionControllerMock = mockery.mock(ServiceActionController.class);

    /** Fixture: principal DAO. */
    private final DomainMapper<String, Principal> principalDao = mockery.mock(DomainMapper.class, "principalDao");

    /**
     * Instance of the CreateOauthRequestToken Service Action.
     */
    private final ServiceAction createOAuthRequestTokenAction = mockery.mock(ServiceAction.class,
            "createOAuthRequestTokenAction");

    /**
     * Instance of the AuthorizeOAuthToken Service Action.
     */
    private final ServiceAction authorizeOAuthTokenAction = mockery.mock(ServiceAction.class,
            "authorizeOAuthTokenAction");

    /**
     * Instance of the UpdateRequestToAccessToken Service Action.
     */
    private final ServiceAction updateRequestToAccessTokenAction = mockery.mock(ServiceAction.class,
            "updateRequestToAccessTokenAction");

    /**
     * Instance of the GetOAuthEntryByToken Service Action.
     */
    private final ServiceAction getOAuthEntryByTokenAction = mockery.mock(ServiceAction.class,
            "getOAuthEntryByTokenAction");

    /**
     * Instance of the DisableOAuthToken Service Action.
     */
    private final ServiceAction disableOAuthTokenAction = mockery.mock(ServiceAction.class, "disableOAuthTokenAction");

    /**
     * Instance of the RemoveOAuthToken Service Action.
     */
    private final ServiceAction removeOAuthTokenAction = mockery.mock(ServiceAction.class, "removeOAuthTokenAction");

    /**
     * Mocked instnace of the ServiceActionContext.
     */
    private final ServiceActionContext serviceActionContextMock = mockery.mock(ServiceActionContext.class);

    /**
     * Instance of the RemoveOAuthToken Service Action.
     */
    private final ServiceAction getOAuthConsumerByConsumerKeyAction = mockery.mock(ServiceAction.class,
            "getOAuthConsumerByConsumerKeyAction");

    /**
     * Instance of the GetSecurity Tocken for Consumer Request Service Action.
     */
    private final ServiceAction getSecurityTokenForConsumerRequestAction = mockery.mock(ServiceAction.class,
            "getSecurityTokenForConsumerRequestAction");

    /** Fixture: principal. */
    private final Principal principal = mockery.mock(Principal.class, "principal");

    /**
     * Set up the system to test.
     */
    @Before
    public void setUp()
    {
        sut = new OAuthDataStoreImpl(createOAuthRequestTokenAction, createOAuthRequestTokenAction,
                createOAuthRequestTokenAction, getOAuthEntryByTokenAction, disableOAuthTokenAction,
                removeOAuthTokenAction, getOAuthConsumerByConsumerKeyAction, getSecurityTokenForConsumerRequestAction,
                principalDao);
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(entry));
            }
        });

        sut.generateRequestToken("key", "1.0", "http://localhost:8080/gadgets/oauthcallback");
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });

        sut.generateRequestToken("key", "1.0", "http://localhost:8080/gadgets/oauthcallback");
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(principalDao).execute(with(any(String.class)));
                will(returnValue(principal));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
            }
        });
        sut.authorizeToken(testEntry, TEST_ARG1);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(principalDao).execute(with(any(String.class)));
                will(returnValue(principal));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });
        sut.authorizeToken(testEntry, TEST_ARG1);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(principalDao).execute(with(any(String.class)));
                will(returnValue(principal));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(entry));
            }
        });

        sut.convertToAccessToken(testEntry);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(principalDao).execute(with(any(String.class)));
                will(returnValue(principal));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });

        sut.convertToAccessToken(testEntry);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(consumer));
            }
        });

        sut.getConsumer(TEST_ARG1);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });

        sut.getConsumer(TEST_ARG1);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(entry));
            }
        });
        sut.getEntry(TEST_ARG1);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });
        sut.getEntry(TEST_ARG1);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
            }
        });

        sut.disableToken(testEntry);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });

        sut.disableToken(testEntry);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
            }
        });

        sut.removeToken(testEntry);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });

        sut.removeToken(testEntry);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(principalDao).execute(with(TEST_ARG1));
                will(returnValue(principal));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(response));
            }
        });
        sut.getSecurityTokenForConsumerRequest(TEST_ARG1, TEST_ARG1);
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(principalDao).execute(with(TEST_ARG1));
                will(returnValue(principal));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });
        sut.getSecurityTokenForConsumerRequest(TEST_ARG1, TEST_ARG1);
        mockery.assertIsSatisfied();
    }
}
