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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.PersistentLogin;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;

/**
 * Tests for ExtendedUserDetailsImpl class.
 *
 */
public class ExtendedUserDetailsImplTest
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
     * Person mock.
     */
    private final Person person = context.mock(Person.class);

    /**
     * Test constructor sets Person.
     */
    @Test
    public void testConstructorNonNullPerson()
    {
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, null);
        assertNotNull("Person not set in constructor", sut.getPerson());
    }

    /**
     * Test constructor sets PersistentLogin.
     */
    @Test
    public void testConstructorNonNullPersistentLogin()
    {
        final PersistentLogin login = context.mock(PersistentLogin.class);

        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, login, null, null);
        assertNotNull("PersistentLogin not set in constructor", sut.getPersistentLogin());
    }

    /**
     * Test constructor will not accept null person parameter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullPerson()
    {
        @SuppressWarnings("unused")
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(null, null, null, null);
    }

    /**
     * Test that getPassword always returns null.
     */
    @Test
    public void testGetPassword()
    {

        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, null);
        assertNull("getPassword should always return null", sut.getPassword());
    }

    /**
     * Test that getUsername returns accountId of person.
     */
    @Test
    public void testGetUsername()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(person).getAccountId();
                will(returnValue("acctid"));
            }
        });

        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, null);
        String foo = sut.getUsername();
        assertEquals("Username not equal to Person object's username", "acctid", foo);
        context.assertIsSatisfied();
    }

    /**
     * Test isAccountNonLocked.
     */
    @Test
    public void testIsAccountNonLockedYes()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(person).isAccountLocked();
                will(returnValue(false));
            }
        });
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, null);
        assertTrue(sut.isAccountNonLocked());
    }

    /**
     * Test isAccountNonLocked.
     */
    @Test
    public void testIsAccountNonLockedNo()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(person).isAccountLocked();
                will(returnValue(true));
            }
        });
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, null);
        assertFalse(sut.isAccountNonLocked());
    }

    /**
     * Test isEnabled.
     */
    @Test
    public void testIsEnabledYes()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(person).isAccountDeactivated();
                will(returnValue(false));
            }
        });
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, null);
        assertTrue(sut.isEnabled());
    }

    /**
     * Test isEnabled.
     */
    @Test
    public void testIsEnabledNo()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(person).isAccountDeactivated();
                will(returnValue(true));
            }
        });
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, null);
        assertFalse(sut.isEnabled());
    }

    /**
     * Test the authentication type getter.
     */
    @Test
    public void testAuthenticationType()
    {
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, AuthenticationType.FORM);
        assertEquals(AuthenticationType.FORM, sut.getAuthenticationType());
    }

    // the rest of tests are somewhat bogus because they are testing hard-coded return values
    // in the sut as they are not implemented in our system yet, but required by Spring's UserDetails
    // interface.

    /**
     * Test getAuthorities doesn't return null.
     */
    @Test
    public void testGetAuthorities()
    {
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, null);
        assertNotNull("getAuthorities method should not return null", sut.getAuthorities());
    }

    /**
     * Test getAuthorities returns same as passed in constructor.
     */
    @Test
    public void testGetAuthoritiesNonNull()
    {
        GrantedAuthority[] auths = new GrantedAuthority[1];
        GrantedAuthority gaMock = context.mock(GrantedAuthority.class);
        auths[0] = gaMock;
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, auths, null);
        assertEquals(1, sut.getAuthorities().length);
        assertEquals(gaMock, sut.getAuthorities()[0]);
    }

    /**
     * Bogus Test for now. Required interface method.
     */
    @Test
    public void testIsAccountNonExpired()
    {
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, null);
        assertTrue(sut.isAccountNonExpired());
    }

    /**
     * Bogus Test for now. Required interface method.
     */
    @Test
    public void testIsCredentialsNonExpired()
    {
        ExtendedUserDetailsImpl sut = new ExtendedUserDetailsImpl(person, null, null, null);
        assertTrue(sut.isCredentialsNonExpired());
    }
}
