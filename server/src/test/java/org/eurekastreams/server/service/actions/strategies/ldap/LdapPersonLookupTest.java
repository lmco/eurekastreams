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
package org.eurekastreams.server.service.actions.strategies.ldap;

import java.util.HashMap;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;

import org.eurekastreams.server.service.actions.strategies.ldap.filters.FilterCreator;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Tests the LDAP person lookup.
 */
public class LdapPersonLookupTest
{

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
     * System under test.
     */
    private LdapPersonLookup sut;

    /**
     * Mock LDAP template.
     */
    private LdapTemplate templateMock = context.mock(LdapTemplate.class);

    /**
     * Mock filter creator.
     */
    private FilterCreator filterCreatorMock = context.mock(FilterCreator.class);

    /**
     * LDAP to Person mapper mock.
     */
    private CollectingNameClassPairCallbackHandler ldapToPersonMapperMock = context
            .mock(CollectingNameClassPairCallbackHandler.class);

    /**
     * People appender.
     */
    private PeopleAppender peopleAppenderMock = context.mock(PeopleAppender.class);

    /**
     * ContextSource mock.
     */
    private LdapContextSource contextSource = context.mock(LdapContextSource.class);

    /**
     * LdapSearchStrategy mock.
     */
    private LdapSearchStrategy ldapSearchStrategyMock = context.mock(LdapSearchStrategy.class);

    /**
     * ContextSource urls.
     */
    private String[] contextSourceUrls = { "ldaps://foo.bar.example.com:3269" };

    /**
     * Setup text fixtures.
     */
    @Before
    public final void setUp()
    {
        HashMap<String, LdapTemplate> templates = new HashMap<String, LdapTemplate>();

        templates.put("domainacct", templateMock);

        sut = new LdapPersonLookup(templates, ldapToPersonMapperMock, peopleAppenderMock, filterCreatorMock,
                ldapSearchStrategyMock);
    }

    /**
     * Tests looking up users.
     *
     * @throws NamingException
     *             shoulnd't be thrown here.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testAttribMapper() throws NamingException
    {
        context.checking(new Expectations()
        {
            {
                // due to logging statements
                allowing(templateMock).getContextSource();
                will(returnValue(contextSource));

                // due to logging statements
                allowing(contextSource).getUrls();
                will(returnValue(contextSourceUrls));

                oneOf(peopleAppenderMock).setPeople(with(any(HashMap.class)));

                oneOf(peopleAppenderMock).setDefaultLdapTemplate(with(any(LdapTemplate.class)));

                oneOf(ldapSearchStrategyMock).searchLdap(with(any(LdapTemplate.class)), with(any(String.class)),
                        with(any(SearchControls.class)), with(any(CollectingNameClassPairCallbackHandler.class)));

                oneOf(filterCreatorMock).getFilter("name exists");
            }
        });

        sut.findPeople("name exists", 0).size();
        context.assertIsSatisfied();
    }

    /**
     * Tests looking up users with a domain.
     *
     * @throws NamingException
     *             shoulnd't be thrown here.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testAttribMapperDomain() throws NamingException
    {
        context.checking(new Expectations()
        {
            {
                // due to logging statements
                allowing(templateMock).getContextSource();
                will(returnValue(contextSource));

                // due to logging statements
                allowing(contextSource).getUrls();
                will(returnValue(contextSourceUrls));

                oneOf(peopleAppenderMock).setPeople(with(any(HashMap.class)));

                oneOf(peopleAppenderMock).setDefaultLdapTemplate(with(any(LdapTemplate.class)));

                oneOf(ldapSearchStrategyMock).searchLdap(with(any(LdapTemplate.class)), with(any(String.class)),
                        with(any(SearchControls.class)), with(any(CollectingNameClassPairCallbackHandler.class)));

                oneOf(filterCreatorMock).getFilter("name exists");
            }
        });

        sut.findPeople("domainacct\\name exists", 0).size();
        context.assertIsSatisfied();
    }
}
