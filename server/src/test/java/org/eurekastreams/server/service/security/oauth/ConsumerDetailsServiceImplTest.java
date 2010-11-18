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
package org.eurekastreams.server.service.security.oauth;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.oauth.OAuthConsumer;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.commons.test.EasyMatcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.provider.ConsumerDetails;
/**
 * Tests ConsumerDetailsServiceImpl.
 */
public class ConsumerDetailsServiceImplTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** SUT. */
    private ConsumerDetailsServiceImpl sut;

    /** Fixture: Action Controller. */
    private final ActionController actionController = context.mock(ActionController.class);

    /** Fixture: Action to get consumer. */
    private final ServiceAction getOAuthConsumerByConsumerKeyAction = context.mock(ServiceAction.class);

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ConsumerDetailsServiceImpl(actionController, getOAuthConsumerByConsumerKeyAction, "ROLE1,ROLE2");
    }

    /**
     * Tests normal retrieval.
     */
    @Test
    public void testLoadConsumerByConsumerKey()
    {
        final OAuthConsumer consumer = new OAuthConsumer("", "KEY", "SECRET", null);

        context.checking(new Expectations()
        {
            {
                oneOf(actionController).execute(with(new EasyMatcher<ServiceActionContext>()
                {
                    @Override
                    protected boolean isMatch(final ServiceActionContext inTestObject)
                    {
                        return "KEY".equals(inTestObject.getParams());
                    }
                }), with(same(getOAuthConsumerByConsumerKeyAction)));
                will(returnValue(consumer));
            }
        });

        ConsumerDetails result = sut.loadConsumerByConsumerKey("KEY");
        context.assertIsSatisfied();

        assertEquals("KEY", result.getConsumerKey());
        assertNotNull(result.getConsumerName());
        assertEquals("SECRET", ((SharedConsumerSecret) result.getSignatureSecret()).getConsumerSecret());
        GrantedAuthority[] auths = result.getAuthorities();
        assertEquals(2, auths.length);
        assertEquals("ROLE1", auths[0].getAuthority());
        assertEquals("ROLE2", auths[1].getAuthority());
    }

    /**
     * Tests failed retrieval.
     */
    @Test(expected = OAuthException.class)
    public void testLoadConsumerByConsumerKeyFailure()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionController).execute(with(any(ServiceActionContext.class)), with(any(ServiceAction.class)));
                will(throwException(new Exception("Failure")));
            }
        });

        ConsumerDetails result = sut.loadConsumerByConsumerKey("KEY");
        context.assertIsSatisfied();
    }
}
