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
package org.eurekastreams.server.service.security.userdetails;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.domain.PersistentLogin;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.eurekastreams.server.service.security.jaas.JaasAuthenticationProviderWrapper;
import org.eurekastreams.server.service.security.persistentlogin.PersistentLoginRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 * Custom implementation of Spring's UserDetailsService interface. Loads user details from data store. Currently returns
 * ExtendedUserDetails object that encapsulates Person and PersistentLogin information (plus the standard UserDetails
 * stuff).
 *
 */
public class UserDetailsServiceImpl implements UserDetailsService
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(JaasAuthenticationProviderWrapper.class);

    /**
     * Mapper for Person information.
     */
    private final PersonMapper personMapper;

    /**
     * Mapper for PersistentLoginInformation.
     */
    private final PersistentLoginRepository loginRepository;

    /**
     * The AuthorityProvider for this service to use.
     */
    private final AuthorityProvider authorityProvider;

    /**
     * Authentication type.
     */
    private AuthenticationType authenticationType = AuthenticationType.NOTSET;

    /**
     * {@link ActionController}.
     */
    private final ActionController serviceActionController;

    /**
     * Action to create user from LDAP.
     */
    private final TaskHandlerServiceAction createUserfromLdapAction;

    /**
     * This username will short circut and fail fast.
     */
    private final String bypassUserName = "<NOTSET>";

    /**
     * Constructor.
     *
     * @param inPersonMapper
     *            The PersonMapper.
     * @param inPersistentLoginRepository
     *            The PersistentLoginMapper.
     * @param inAuthorityProvider
     *            The AuthorityProvider to use.
     * @param inServiceActionController
     *            {@link ActionController}.
     * @param inCreateUserfromLdapAction
     *            Action to create user from LDAP.
     */
    public UserDetailsServiceImpl(final PersonMapper inPersonMapper,
            final PersistentLoginRepository inPersistentLoginRepository, final AuthorityProvider inAuthorityProvider,
            final ActionController inServiceActionController, final TaskHandlerServiceAction inCreateUserfromLdapAction)
    {
        Assert.notNull(inPersonMapper);
        personMapper = inPersonMapper;
        loginRepository = inPersistentLoginRepository;
        authorityProvider = inAuthorityProvider;
        serviceActionController = inServiceActionController;
        createUserfromLdapAction = inCreateUserfromLdapAction;
    }

    /**
     * Returns populated UserDetails object for user.
     *
     * @param username
     *            The username.
     * @return Populated UserDetails object for user.
     */
    @Override
    public UserDetails loadUserByUsername(final String username)
    {
        if (bypassUserName.equalsIgnoreCase(username))
        {
            String errorMessage = "Configured to skip loading user details for " + username;
            log.debug(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }

        Person person = null;
        PersistentLogin login = null;
        List<GrantedAuthority> authorities = null;
        try
        {
            person = personMapper.findByAccountId(username);
            login = (loginRepository == null) ? null : loginRepository.getPersistentLogin(username);
            authorities = (authorityProvider == null) ? new ArrayList<GrantedAuthority>(0) : authorityProvider
                    .loadAuthoritiesByUsername(username);

            // if user not found in DB, try to create from LDAP
            if (person == null)
            {
                person = (Person) serviceActionController.execute(new ServiceActionContext(username, null),
                        createUserfromLdapAction);
            }
        }
        catch (Exception e)
        {
            String errorMessage = "Error loading user details for: " + username;
            log.error(errorMessage + " " + e.getMessage());

            throw new DataRetrievalFailureException(errorMessage, e);
        }

        // If user still not found, give up.
        if (person == null)
        {
            String errorMessage = "User not found: " + username;
            log.info(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }

        return new ExtendedUserDetailsImpl(person, login,
                authorities.toArray(new GrantedAuthority[authorities.size()]), authenticationType);
    }

    /**
     * @param inAuthenticationType
     *            the authenticationType to set
     */
    public void setAuthenticationType(final AuthenticationType inAuthenticationType)
    {
        authenticationType = inAuthenticationType;
    }
}
