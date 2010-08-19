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
package org.eurekastreams.server.persistence.mappers.ldap;

import javax.naming.directory.SearchControls;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Test suite for the {@link DefaultLdapSearchStrategy}.
 * 
 */
public class DefaultLdapSearchStrategyTest
{
    /**
     * System under test.
     */
    private DefaultLdapSearchStrategy sut;

    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked instance of {@link LdapTemplate} for this test suite.
     */
    private LdapTemplate ldapTemplateMock = context.mock(LdapTemplate.class);

    /**
     * Mocked instnace of {@link SearchControls} for this test suite.
     */
    private SearchControls searchControlsMock = context.mock(SearchControls.class);

    /**
     * Mocked instance of {@link CollectingNameClassPairCallbackHandler} for this test suite.
     */
    private CollectingNameClassPairCallbackHandler handlerMock = context
            .mock(CollectingNameClassPairCallbackHandler.class);

    /**
     * Prepare the test suite.
     */
    @Before
    public void setup()
    {
        sut = new DefaultLdapSearchStrategy();
    }

    /**
     * Test the successful path through the ldap search.
     */
    @Test
    public void testSearchLdap()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(ldapTemplateMock).search(with(any(String.class)), with(any(String.class)),
                        with(any(SearchControls.class)), with(any(CollectingNameClassPairCallbackHandler.class)));

                oneOf(handlerMock).getList();
            }
        });
        sut.searchLdap(ldapTemplateMock, "cn=groupname", searchControlsMock, handlerMock);

        context.assertIsSatisfied();
    }
}
