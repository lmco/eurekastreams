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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.eurekastreams.server.service.security.userdetails.TermsOfServiceAcceptanceStrategy;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Strategy to get current user's {@link PersonModelView}.
 * 
 */
public class GetPersonModelViewExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to get the system admin ids.
     */
    private DomainMapper<Serializable, List<Long>> systemAdminIdsMapper;

    /**
     * Person Mapper used to retrieve PersonModelView from accountId.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * Terms of service acceptance strategy.
     */
    private TermsOfServiceAcceptanceStrategy toSAcceptanceStrategy;

    /**
     * {@link ActionController}.
     */
    private final ActionController serviceActionController;

    /**
     * Action to create user from LDAP.
     */
    private final TaskHandlerServiceAction createUserfromLdapAction;

    /**
     * Constructor that sets up the mapper.
     * 
     * @param inSystemAdminIdsMapper
     *            mapper to get the system administrator ids
     * @param inGetPersonModelViewByAccountIdMapper
     *            - mapper to get a PersonModelView by account id
     * @param inTosAcceptanceStrategy
     *            the strategy to check if the user's terms of service acceptance is current
     * @param inServiceActionController
     *            {@link ActionController}.
     * @param inCreateUserfromLdapAction
     *            Action to create user from LDAP.
     */
    public GetPersonModelViewExecution(final DomainMapper<Serializable, List<Long>> inSystemAdminIdsMapper,
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper,
            final TermsOfServiceAcceptanceStrategy inTosAcceptanceStrategy,
            final ActionController inServiceActionController, final TaskHandlerServiceAction inCreateUserfromLdapAction)
    {
        systemAdminIdsMapper = inSystemAdminIdsMapper;
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
        toSAcceptanceStrategy = inTosAcceptanceStrategy;
        serviceActionController = inServiceActionController;
        createUserfromLdapAction = inCreateUserfromLdapAction;
    }

    /**
     * Get current user's {@link PersonModelView}. This includes setting the ToSAcceptance and authentication type
     * properties.
     * 
     * @param inActionContext
     *            action context.
     * @return {@link PersonModelView}.
     */
    @Override
    public PersonModelView execute(final PrincipalActionContext inActionContext)
    {
        String accountId = (String) inActionContext.getParams();

        if (accountId == null)
        {
            accountId = inActionContext.getPrincipal().getAccountId();
            log.debug("no account id in the params - using current user's account id: " + accountId);
        }

        PersonModelView person = null;
        try
        {
            person = getPersonModelViewByAccountIdMapper.execute(accountId);
        }
        catch (Exception e)
        {
            log.debug("Exception loading person" + e);
        }

        // if user not found in DB, try to create from LDAP
        if (person == null)
        {
            try
            {
                person = ((Person) serviceActionController.execute(new ServiceActionContext(accountId, null),
                        createUserfromLdapAction)).toPersonModelView();
            }
            catch (Exception e)
            {
                log.debug("Exception loading person" + e);
            }
        }

        // If we're still null, just return null.
        if (person == null)
        {
            return null;
        }

        List<Long> systemAdminIds = systemAdminIdsMapper.execute(null);

        if (systemAdminIds.contains(person.getEntityId()))
        {
            log.debug("user " + accountId + " is a root org coordinator.");
            person.getRoles().add((Role.ORG_COORDINATOR));
            person.getRoles().add((Role.ROOT_ORG_COORDINATOR));
        }

        ExtendedUserDetails userDetails = (ExtendedUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        person.setTosAcceptance(toSAcceptanceStrategy.isValidTermsOfServiceAcceptanceDate(person
                .getLastAcceptedTermsOfService()));
        person.setAuthenticationType(userDetails.getAuthenticationType());

        log.debug("Found banner for " + accountId + " - " + person.getBannerId());

        return person;
    }
}
