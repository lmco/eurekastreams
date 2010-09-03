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

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetOAuthConsumerByConsumerKeyExecution} class.
 * 
 */
@SuppressWarnings("unchecked")
public class GetOAuthConsumerByConsumerKeyExecutionTest
{
    /**
     * System under test.
     */
    private GetOAuthConsumerByConsumerKeyExecution sut;

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
     * Mocked instance of DomainMapper.
     */
    private DomainMapper<String, OAuthConsumer> consumerMapper = context.mock(DomainMapper.class);

    /**
     * Mocked instance of the action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * test request url.
     */
    private static final String TEST_REQUEST_TOKEN_URL = "http://example.com/requesttoken";

    /**
     * test authorize url.
     */
    private static final String TEST_AUTHORIZE_URL = "http://example.com/authorize";

    /**
     * test access token url.
     */
    private static final String TEST_ACCESS_TOKEN_URL = "http://example.com/accesstoken";

    /**
     * Test consumer key.
     */
    private static final String TEST_CONSUMER_KEY = "testkey";

    /**
     * Prep the sut.
     */
    @Before
    public void setup()
    {
        sut = new GetOAuthConsumerByConsumerKeyExecution(consumerMapper, TEST_REQUEST_TOKEN_URL, TEST_AUTHORIZE_URL,
                TEST_ACCESS_TOKEN_URL);
    }

    /**
     * Test grabbing the consumer key.
     */
    @Test
    public void testSuccessfulConsumerKey()
    {
        final OAuthConsumer consumer = new OAuthConsumer("testprovider", "gadgeturl", TEST_CONSUMER_KEY, "secret",
                "HMAC-SHA1");
        
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(TEST_CONSUMER_KEY));

                oneOf(consumerMapper).execute(TEST_CONSUMER_KEY);
                will(returnValue(consumer));
            }
        });

        net.oauth.OAuthConsumer results = (net.oauth.OAuthConsumer) sut.execute(actionContext);
        assertEquals(results.consumerKey, TEST_CONSUMER_KEY);
        assertEquals(results.consumerSecret, "secret");
        context.assertIsSatisfied();
    }
}
