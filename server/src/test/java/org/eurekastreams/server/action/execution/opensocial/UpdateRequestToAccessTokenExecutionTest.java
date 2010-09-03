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

import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link UpdateRequestToAccessTokenExecution}.
 * 
 */
@SuppressWarnings("unchecked")
public class UpdateRequestToAccessTokenExecutionTest
{
    /**
     * System under test.
     */
    private UpdateRequestToAccessTokenExecution sut;

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
     * Instance of OAuth entry mapper injected by spring to delete an entry.
     */
    private final DomainMapper<String, Boolean> deleteMapper = context.mock(DomainMapper.class);

    /**
     * Instance of OAuth entry mapper injected by spring to insert an entry.
     */
    private final InsertMapper<OAuthDomainEntry> insertMapper = context.mock(InsertMapper.class);

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
        sut = new UpdateRequestToAccessTokenExecution(deleteMapper, insertMapper, conversionStrat);
    }

    /**
     * Test successful conversion of access token.
     */
    @Test
    public void testConversion()
    {
        final OAuthEntry requestEntry = new OAuthEntry();
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(requestEntry));

                oneOf(deleteMapper).execute(with(any(String.class)));

                oneOf(conversionStrat).convertToEntryDTO(with(any(OAuthEntry.class)));

                oneOf(insertMapper).execute(with(any(PersistenceRequest.class)));
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
