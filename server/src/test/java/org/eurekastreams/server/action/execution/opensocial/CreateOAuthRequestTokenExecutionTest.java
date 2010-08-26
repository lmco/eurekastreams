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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.opensocial.CreateOAuthRequestTokenRequest;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.OAuthEntryMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link CreateOAuthRequestTokenExecution} class.
 *
 */
public class CreateOAuthRequestTokenExecutionTest
{
    /**
     * System under test.
     */
    private CreateOAuthRequestTokenExecution sut;

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
     * Instance of OAuth entry mapper injected by spring.
     */
    private OAuthEntryMapper entryMapper = context.mock(OAuthEntryMapper.class);

    /**
     * Strategy for converting {@link OAuthEntry} objects to {@link OAuthDomainEntry} objects.
     */
    private OAuthEntryConversionStrategy oauthConversionStrat = context.mock(OAuthEntryConversionStrategy.class);

    /**
     * Mocked instance of the action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Instance of the OAuthDomain for this execution example: samplecontainer.com.
     */
    private static final String TEST_OAUTH_DOMAIN = "testdomain.com";

    /**
     * Instance of the OAuthContainer for this execution example: default.
     */
    private static final String TEST_OAUTH_CONTAINER = "testcontainer";

    /**
     * Test callback url.
     */
    private static final String TEST_CALLBACK_URL = "http://example.org/callbackurl";

    /**
     * Test oauth version.
     */
    private static final String TEST_OAUTH_VERSION = "1.0";

    /**
     * Test Consumer key.
     */
    private static final String TEST_CONSUMER_KEY = "testconsumerkey";

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new CreateOAuthRequestTokenExecution(TEST_OAUTH_DOMAIN, TEST_OAUTH_CONTAINER, entryMapper,
                oauthConversionStrat);
    }

    /**
     * Test successful token creation.
     */
    @Test
    public void testSuccessfulTokenCreation()
    {
        final CreateOAuthRequestTokenRequest request = new CreateOAuthRequestTokenRequest(TEST_CONSUMER_KEY,
                TEST_OAUTH_VERSION, TEST_CALLBACK_URL);
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(request));

                oneOf(oauthConversionStrat).convertToEntryDTO(with(any(OAuthEntry.class)));

                oneOf(entryMapper).insert(with(any(OAuthDomainEntry.class)));
            }
        });

        OAuthEntry results = sut.execute(actionContext);
        assertNotNull(results);
        assertEquals(results.callbackUrl, TEST_CALLBACK_URL);
        assertEquals(results.consumerKey, TEST_CONSUMER_KEY);
        assertEquals(results.oauthVersion, TEST_OAUTH_VERSION);
        assertEquals(results.type, OAuthEntry.Type.REQUEST);
        assertNotNull(results.token);
        assertNotNull(results.tokenSecret);
        assertEquals(results.container, TEST_OAUTH_CONTAINER);
        assertEquals(results.domain, TEST_OAUTH_DOMAIN);
        assertEquals(results.callbackUrl, TEST_CALLBACK_URL);
        assertEquals(results.callbackUrlSigned, true);

        context.assertIsSatisfied();
    }
}
