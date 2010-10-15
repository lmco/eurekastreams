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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashSet;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.eurekastreams.server.service.security.userdetails.TermsOfServiceAcceptanceStrategy;
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

/**
 * Test for GetPersonModelViewExecution class.
 * 
 */
public class GetPersonModelViewExecutionTest
{
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
    private SecurityContext securityContext = context.mock(SecurityContext.class);

    /**
     * {@link Authentication} mock.
     */
    private Authentication authentication = context.mock(Authentication.class);

    /**
     * {@link ExtendedUserDetails} mock.
     */
    private ExtendedUserDetails userDetails = context.mock(ExtendedUserDetails.class);

    /**
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal} mock.
     */
    private Principal actionContextPrincipal = context.mock(Principal.class);

    /**
     * Person Mapper used to retrieve PersonModelView from accountId.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = context.mock(
            DomainMapper.class, "getPersonModelViewByAccountIdMapper");

    /**
     * {@link GetRootOrganizationIdAndShortName} mock.
     */
    private GetRootOrganizationIdAndShortName rootOrgDAO = context.mock(GetRootOrganizationIdAndShortName.class);

    /**
     * {@link GetRecursiveOrgCoordinators} mock.
     */
    private GetRecursiveOrgCoordinators orgCoordinatorDAO = context.mock(GetRecursiveOrgCoordinators.class);

    /**
     * {@link GetRecursiveOrgCoordinators} mock.
     */
    private GetRecursiveOrgCoordinators orgCoordinatorDAOUp = context.mock(GetRecursiveOrgCoordinators.class, "up");

    /**
     * Terms of service acceptance strategy.
     */
    private TermsOfServiceAcceptanceStrategy toSAcceptanceStrategy = context
            .mock(TermsOfServiceAcceptanceStrategy.class);

    /**
     * User account id for tests.
     */
    private String accountId = "accountid";

    /**
     * System under test.
     */
    private GetPersonModelViewExecution sut = new GetPersonModelViewExecution(orgCoordinatorDAO, orgCoordinatorDAOUp,
            rootOrgDAO, getPersonModelViewByAccountIdMapper, toSAcceptanceStrategy);

    /**
     * Pre-test setup.
     */
    @Before
    public void setUp()
    {
        originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);
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
     * Perform Action as an org coordinator test.
     * 
     * @throws Exception
     *             the exception.
     */
    @Test
    public void performActionAsOrgCoordinator() throws Exception
    {
        final PersonModelView retPerson = new PersonModelView();
        retPerson.setEntityId(4L);
        retPerson.setRoles(new HashSet<Role>());

        final Date personLastAcceptedTOSDate = new Date();
        retPerson.setLastAcceptedTermsOfService(personLastAcceptedTOSDate);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(actionContextPrincipal));

                allowing(actionContextPrincipal).getAccountId();
                will(returnValue(accountId));

                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(returnValue(userDetails));

                oneOf(userDetails).getAuthenticationType();
                will(returnValue(AuthenticationType.NOTSET));

                oneOf(getPersonModelViewByAccountIdMapper).execute(accountId);
                will(returnValue(retPerson));

                oneOf(toSAcceptanceStrategy).isValidTermsOfServiceAcceptanceDate(with(personLastAcceptedTOSDate));
                will(returnValue(true));

                allowing(rootOrgDAO).getRootOrganizationId();
                will(returnValue(0L));

                oneOf(orgCoordinatorDAO).isOrgCoordinatorRecursively(4L, 0L);
                will(returnValue(true));

                oneOf(orgCoordinatorDAOUp).isOrgCoordinatorRecursively(4L, 0L);
                will(returnValue(true));
            }
        });

        PersonModelView result = sut.execute(actionContext);

        assertEquals(true, result.getRoles().contains(Role.ORG_COORDINATOR));
        assertEquals(true, result.getRoles().contains(Role.ROOT_ORG_COORDINATOR));
        assertEquals(true, result.getTosAcceptance());
        assertEquals(AuthenticationType.NOTSET, result.getAuthenticationType());

        context.assertIsSatisfied();
    }

    /**
     * Perform Action not as an org coordinator test.
     * 
     * @throws Exception
     *             the exception.
     */
    @Test
    public void performActionNotAsOrgCoordinator() throws Exception
    {
        final PersonModelView retPerson = new PersonModelView();
        retPerson.setRoles(new HashSet<Role>());
        retPerson.setEntityId(4L);
        final Date personLastAcceptedTOSDate = new Date();
        retPerson.setLastAcceptedTermsOfService(personLastAcceptedTOSDate);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(actionContextPrincipal));

                allowing(actionContextPrincipal).getAccountId();
                will(returnValue(accountId));

                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(returnValue(userDetails));

                oneOf(userDetails).getAuthenticationType();
                will(returnValue(AuthenticationType.FORM));

                oneOf(getPersonModelViewByAccountIdMapper).execute(accountId);
                will(returnValue(retPerson));

                oneOf(toSAcceptanceStrategy).isValidTermsOfServiceAcceptanceDate(with(personLastAcceptedTOSDate));
                will(returnValue(false));

                allowing(rootOrgDAO).getRootOrganizationId();
                will(returnValue(0L));

                oneOf(orgCoordinatorDAO).isOrgCoordinatorRecursively(4L, 0L);
                will(returnValue(false));

                oneOf(orgCoordinatorDAOUp).isOrgCoordinatorRecursively(4L, 0L);
                will(returnValue(false));

            }
        });

        PersonModelView result = sut.execute(actionContext);

        assertEquals(false, result.getRoles().contains(Role.ORG_COORDINATOR));
        assertEquals(false, result.getRoles().contains(Role.ROOT_ORG_COORDINATOR));
        assertEquals(false, result.getTosAcceptance());
        assertEquals(AuthenticationType.FORM, result.getAuthenticationType());

        context.assertIsSatisfied();
    }

}
