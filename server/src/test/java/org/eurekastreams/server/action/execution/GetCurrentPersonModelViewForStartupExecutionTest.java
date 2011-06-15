/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.eurekastreams.server.service.security.userdetails.TermsOfServiceAcceptanceStrategy;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.oauth.provider.BaseConsumerDetails;

/**
 * Test for GetCurrentPersonModelViewForStartupExecution class.
 */
public class GetCurrentPersonModelViewForStartupExecutionTest
{
    /** Test data. */
    private static final long PERSON_ID = 4L;

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
     * Original {@link SecurityContext}.
     */
    private SecurityContext originalSecurityContext;

    /**
     * {@link SecurityContext} mock.
     */
    private final SecurityContext securityContext = context.mock(SecurityContext.class);

    /**
     * {@link Authentication} mock.
     */
    private final Authentication authentication = context.mock(Authentication.class);

    /**
     * {@link ExtendedUserDetails} mock.
     */
    private final ExtendedUserDetails userDetails = context.mock(ExtendedUserDetails.class);

    /**
     * Person Mapper used to retrieve PersonModelView from accountId.
     */
    private final DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = context.mock(
            DomainMapper.class, "getPersonModelViewByAccountIdMapper");

    /**
     * Mapper to get the system admin ids.
     */
    private final DomainMapper<Serializable, List<Long>> systemAdminIdsMapper = context.mock(DomainMapper.class,
            "systemAdminIdsMapper");

    /**
     * Terms of service acceptance strategy.
     */
    private final TermsOfServiceAcceptanceStrategy toSAcceptanceStrategy = context
            .mock(TermsOfServiceAcceptanceStrategy.class);

    /**
     * User account id for tests.
     */
    private static final String ACCOUNT_ID = "accountid";

    /**
     * System under test.
     */
    private final ExecutionStrategy<PrincipalActionContext> sut = new GetCurrentPersonModelViewForStartupExecution(
            systemAdminIdsMapper, getPersonModelViewByAccountIdMapper, toSAcceptanceStrategy);

    /**
     * Pre-test setup.
     */
    @Before
    public void setUp()
    {
        originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                allowing(securityContext).getAuthentication();
                will(returnValue(authentication));
            }
        });
    }

    /**
     * Post-test clean-up.
     */
    @After
    public void tearDown()
    {
        SecurityContextHolder.setContext(originalSecurityContext);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteAsSystemAdmin()
    {
        final PersonModelView retPerson = new PersonModelView();
        retPerson.setEntityId(PERSON_ID);

        final Date personLastAcceptedTOSDate = new Date();
        retPerson.setLastAcceptedTermsOfService(personLastAcceptedTOSDate);

        context.checking(new Expectations()
        {
            {
                allowing(authentication).getPrincipal();
                will(returnValue(null));

                allowing(getPersonModelViewByAccountIdMapper).execute(ACCOUNT_ID);
                will(returnValue(retPerson));

                allowing(toSAcceptanceStrategy).isValidTermsOfServiceAcceptanceDate(with(personLastAcceptedTOSDate));
                will(returnValue(true));

                allowing(systemAdminIdsMapper).execute(null);
                will(returnValue(Collections.singletonList(PERSON_ID)));
            }
        });

        PrincipalActionContext actionContext = TestContextCreator.createPrincipalActionContext(null, ACCOUNT_ID, 0);

        PersonModelView result = (PersonModelView) sut.execute(actionContext);

        context.assertIsSatisfied();

        assertTrue(result.getRoles().contains(Role.SYSTEM_ADMIN));
        assertTrue(result.getTosAcceptance());
        assertEquals(AuthenticationType.NOTSET, result.getAuthenticationType());
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteNotSystemAdmin()
    {
        final PersonModelView retPerson = new PersonModelView();
        retPerson.setEntityId(PERSON_ID);

        final Date personLastAcceptedTOSDate = new Date();
        retPerson.setLastAcceptedTermsOfService(personLastAcceptedTOSDate);

        context.checking(new Expectations()
        {
            {
                allowing(authentication).getPrincipal();
                will(returnValue(null));

                allowing(getPersonModelViewByAccountIdMapper).execute(ACCOUNT_ID);
                will(returnValue(retPerson));

                allowing(toSAcceptanceStrategy).isValidTermsOfServiceAcceptanceDate(with(personLastAcceptedTOSDate));
                will(returnValue(true));

                allowing(systemAdminIdsMapper).execute(null);
                will(returnValue(Collections.singletonList(9L)));
            }
        });

        PrincipalActionContext actionContext = TestContextCreator.createPrincipalActionContext(null, ACCOUNT_ID, 0);

        PersonModelView result = (PersonModelView) sut.execute(actionContext);

        context.assertIsSatisfied();

        assertFalse(result.getRoles().contains(Role.SYSTEM_ADMIN));
        assertTrue(result.getTosAcceptance());
        assertEquals(AuthenticationType.NOTSET, result.getAuthenticationType());
    }

    /**
     * Core of auth type tests.
     *
     * @param principal
     *            Principal object to use.
     * @param expectedAuthType
     *            Expected auth type.
     */
    private void coreAuthTest(final Object principal, final AuthenticationType expectedAuthType)
    {
        final PersonModelView retPerson = new PersonModelView();
        retPerson.setEntityId(PERSON_ID);

        final Date personLastAcceptedTOSDate = new Date();
        retPerson.setLastAcceptedTermsOfService(personLastAcceptedTOSDate);

        context.checking(new Expectations()
        {
            {
                allowing(authentication).getPrincipal();
                will(returnValue(principal));

                allowing(getPersonModelViewByAccountIdMapper).execute(ACCOUNT_ID);
                will(returnValue(retPerson));

                allowing(toSAcceptanceStrategy).isValidTermsOfServiceAcceptanceDate(with(personLastAcceptedTOSDate));
                will(returnValue(true));

                allowing(systemAdminIdsMapper).execute(null);
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        PrincipalActionContext actionContext = TestContextCreator.createPrincipalActionContext(null, ACCOUNT_ID, 0);

        PersonModelView result = (PersonModelView) sut.execute(actionContext);

        context.assertIsSatisfied();

        assertEquals(expectedAuthType, result.getAuthenticationType());
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteAuthExplicitNotSet()
    {
        context.checking(new Expectations()
        {
            {
                allowing(userDetails).getAuthenticationType();
                will(returnValue(AuthenticationType.NOTSET));
            }
        });

        coreAuthTest(userDetails, AuthenticationType.NOTSET);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteAuthForm()
    {
        context.checking(new Expectations()
        {
            {
                allowing(userDetails).getAuthenticationType();
                will(returnValue(AuthenticationType.FORM));
            }
        });

        coreAuthTest(userDetails, AuthenticationType.FORM);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteAuthOtherType()
    {
        final BaseConsumerDetails consumerDetails = context.mock(BaseConsumerDetails.class);
        coreAuthTest(consumerDetails, AuthenticationType.NOTSET);
    }
}
