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

import static org.junit.Assert.assertNotNull;
import junit.framework.Assert;

import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link GetOAuthEntryByTokenExecution}.
 * 
 */
@SuppressWarnings("unchecked")
public class GetOAuthEntryByTokenExecutionTest
{
    /**
     * System under test.
     */
    private GetOAuthEntryByTokenExecution sut;

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
    private final DomainMapper<String, OAuthDomainEntry> entryMapper = context.mock(DomainMapper.class);

    /**
     * Instance of {@link OAuthEntryConversionStrategy} for this class.
     */
    private final OAuthEntryConversionStrategy conversionStrat = context.mock(OAuthEntryConversionStrategy.class);

    /**
     * Mocked instance of the action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new GetOAuthEntryByTokenExecution(entryMapper, conversionStrat);
    }

    /**
     * Test successful retrieving OAuthToken.
     */
    @Test
    public void testRetrievingOauthToken()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue("token"));

                oneOf(entryMapper).execute("token");
                will(returnValue(new OAuthDomainEntry()));

                oneOf(conversionStrat).convertToEntry(with(any(OAuthDomainEntry.class)));
            }
        });

        OAuthEntry results = (OAuthEntry) sut.execute(actionContext);

        assertNotNull(results);

        context.assertIsSatisfied();
    }

    /**
     * Test retrieving a null OAuthToken.
     */
    @Test
    public void testRetrievingNullOauthToken()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue("token"));

                oneOf(entryMapper).execute("token");
                will(returnValue(null));
            }
        });

        OAuthEntry results = (OAuthEntry) sut.execute(actionContext);

        Assert.assertNull(results);

        context.assertIsSatisfied();
    }

}
