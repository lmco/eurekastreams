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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
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
public class GetCurrentPersonModelViewForStartupExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to get the system admin ids.
     */
    private final DomainMapper<Serializable, List<Long>> systemAdminIdsMapper;

    /**
     * Person Mapper used to retrieve PersonModelView from accountId.
     */
    private final DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * Terms of service acceptance strategy.
     */
    private final TermsOfServiceAcceptanceStrategy toSAcceptanceStrategy;


    /**
     * Constructor that sets up the mapper.
     *
     * @param inSystemAdminIdsMapper
     *            mapper to get the system administrator ids
     * @param inGetPersonModelViewByAccountIdMapper
     *            - mapper to get a PersonModelView by account id
     * @param inTosAcceptanceStrategy
     *            the strategy to check if the user's terms of service acceptance is current
     */
    public GetCurrentPersonModelViewForStartupExecution(
            final DomainMapper<Serializable, List<Long>> inSystemAdminIdsMapper,
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper,
            final TermsOfServiceAcceptanceStrategy inTosAcceptanceStrategy)
    {
        systemAdminIdsMapper = inSystemAdminIdsMapper;
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
        toSAcceptanceStrategy = inTosAcceptanceStrategy;
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
        String accountId = inActionContext.getPrincipal().getAccountId();

        PersonModelView person = getPersonModelViewByAccountIdMapper.execute(accountId);

        List<Long> systemAdminIds = systemAdminIdsMapper.execute(null);
        if (systemAdminIds.contains(person.getEntityId()))
        {
            log.debug("User " + accountId + " is a system administrator.");
            person.getRoles().add(Role.SYSTEM_ADMIN);
        }

        person.setTosAcceptance(toSAcceptanceStrategy.isValidTermsOfServiceAcceptanceDate(person
                .getLastAcceptedTermsOfService()));

        // set authentication type (for determining whether to display a "log out" option)
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof ExtendedUserDetails)
        {
            person.setAuthenticationType(((ExtendedUserDetails) principal).getAuthenticationType());
        }

        return person;
    }
}
