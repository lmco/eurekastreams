/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.PersistentLogin;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.springframework.security.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * UserDetails implementation that extends Spring's UserDetails to allow access to Person and PersistentLogin object.
 *
 */
@SuppressWarnings("serial")
public class ExtendedUserDetailsImpl implements ExtendedUserDetails
{
    /**
     * The person object.
     */
    private Person person = null;

    /**
     * The PersistentLogin object.
     */
    private PersistentLogin persistentLogin = null;

    /**
     * Granted authorities for authenticated user.
     */
    private GrantedAuthority[] grantedAuthorities = null;

    /**
     * Authentication type.
     */
    private final AuthenticationType authenticationType;

    /**
     * Constructor.
     *
     * @param inPerson
     *            The person object.
     * @param inLogin
     *            The PersistentLogin object.
     * @param inGrantedAuthorities
     *            The granted authorities for the user.
     * @param inAuthenticationType
     *            Authentication type.
     *
     */
    public ExtendedUserDetailsImpl(final Person inPerson, final PersistentLogin inLogin,
            final GrantedAuthority[] inGrantedAuthorities, final AuthenticationType inAuthenticationType)
    {
        Assert.notNull(inPerson);
        person = inPerson;
        persistentLogin = inLogin;
        grantedAuthorities = (inGrantedAuthorities == null) ? new GrantedAuthority[0] : inGrantedAuthorities;
        authenticationType = (inAuthenticationType == null) ? AuthenticationType.NOTSET : inAuthenticationType;

    }

    /**
     * Getter for PersistentLogin.
     *
     * @return The PersistentLogin object.
     */
    public PersistentLogin getPersistentLogin()
    {
        return persistentLogin;
    }

    /**
     * Getter for Person.
     *
     * @return The person object.
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * Getter for GrantedAuthorities.
     *
     * @return The GrantedAuthorities array.
     */
    public GrantedAuthority[] getAuthorities()
    {
        return grantedAuthorities;
    }

    /**
     * Getter for password.
     *
     * @return always null.
     */
    public String getPassword()
    {
        return null;
    }

    /**
     * Getter for username.
     *
     * @return The person object's accountId or null if person is null
     */
    public String getUsername()
    {
        return person.getAccountId();
    }

    /**
     * Determine accountNonExpired value.
     *
     * @return accountNonExpired boolean value.
     */
    public boolean isAccountNonExpired()
    {
        return true;
    }

    /**
     * @return If the account is not locked.
     */
    public boolean isAccountNonLocked()
    {
        return !person.isAccountLocked();
    }

    /**
     * Determine credentialsNonExpired value.
     *
     * @return credentialsNonExpired boolean value.
     */
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    /**
     * @return If the account has not been deactivated.
     */
    public boolean isEnabled()
    {
        return !person.isAccountDeactivated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthenticationType getAuthenticationType()
    {
        return authenticationType;
    }

}
