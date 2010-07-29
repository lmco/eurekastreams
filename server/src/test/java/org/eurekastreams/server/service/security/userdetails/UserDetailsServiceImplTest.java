/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.eurekastreams.server.service.security.persistentlogin.PersistentLoginRepository;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;

/**
 * Test class for UserDetailsServiceImpl.
 *
 */
public class UserDetailsServiceImplTest
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
     * Test constructor rejects null PersonMapper.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullPersonMapper()
    {
        new UserDetailsServiceImpl(null, null, null);
    }

    /**
     * Test mappers are called correctly and non-null is returned.
     */
    @Test
    public void testLoadUserByUsername()
    {
        final PersonMapper personMapper = context.mock(PersonMapper.class);
        final PersistentLoginRepository loginRepo = context.mock(PersistentLoginRepository.class);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByAccountId(with("username"));

                oneOf(loginRepo).getPersistentLogin(with("username"));
            }
        });

        UserDetailsServiceImpl sut = new UserDetailsServiceImpl(personMapper, loginRepo, null);
        sut.setAuthenticationType(AuthenticationType.FORM);
        ExtendedUserDetails result = (ExtendedUserDetails) sut.loadUserByUsername("username");
        assertNotNull("Should return UserDetailsObject", result);
        assertEquals(AuthenticationType.FORM, result.getAuthenticationType());
        context.assertIsSatisfied();
    }

    /**
     * Test mappers are called correctly and non-null is returned.
     */
    @Test
    public void testLoadUserByUsernameWithAuthorityProvider()
    {
        final PersonMapper personMapper = context.mock(PersonMapper.class);
        final PersistentLoginRepository loginRepo = context.mock(PersistentLoginRepository.class);
        final AuthorityProvider authProvider = context.mock(AuthorityProvider.class);
        final ArrayList<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        auths.add(context.mock(GrantedAuthority.class));

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByAccountId(with("username"));

                oneOf(loginRepo).getPersistentLogin(with("username"));

                oneOf(authProvider).loadAuthoritiesByUsername("username");
                will(returnValue(auths));
            }
        });

        UserDetailsServiceImpl sut = new UserDetailsServiceImpl(personMapper, loginRepo, authProvider);
        UserDetails result = sut.loadUserByUsername("username");
        assertNotNull("Should return UserDetailsObject", result);
        assertEquals(1, result.getAuthorities().length);
        context.assertIsSatisfied();
    }

    /**
     * Test that expected exception is tossed if mappers throw exception.
     */
    @Test(expected = DataRetrievalFailureException.class)
    public void testLoadUserByUsernameDataException()
    {
        final PersonMapper personMapper = context.mock(PersonMapper.class);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByAccountId(with("username"));
                will(throwException(new Exception()));
            }
        });

        UserDetailsServiceImpl sut = new UserDetailsServiceImpl(personMapper, null, null);
        sut.loadUserByUsername("username");
        context.assertIsSatisfied();
    }

    /**
     * Test that expected exception is tossed if username not found in data store.
     */
    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsernamePersonNotFound()
    {
        final PersonMapper personMapper = context.mock(PersonMapper.class);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByAccountId(with("username"));
                will(returnValue(null));
            }
        });

        UserDetailsServiceImpl sut = new UserDetailsServiceImpl(personMapper, null, null);
        sut.loadUserByUsername("username");
        context.assertIsSatisfied();
    }

}
