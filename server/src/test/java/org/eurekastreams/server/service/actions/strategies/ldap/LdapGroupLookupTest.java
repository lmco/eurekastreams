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
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Tests the LDAP group lookup.
 */
public class LdapGroupLookupTest
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
    private LdapGroupLookup sut;

    /**
     * Mock LDAP template.
     */
    private LdapTemplate templateMock = context.mock(LdapTemplate.class);

    /**
     * Mock filter creator.
     */
    private FilterCreator filterCreatorMock = context.mock(FilterCreator.class);

    /**
     * ContextSource mock.
     */
    private LdapContextSource contextSource = context.mock(LdapContextSource.class);

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

        templates.put("domainname", templateMock);

        sut = new LdapGroupLookup(templates, filterCreatorMock);
    }

    /**
     * Tests looking up group.
     *
     * @throws NamingException
     *             shouldn't be thrown here.
     */
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

                oneOf(filterCreatorMock).getFilter("sAMAccountName=name exists");

                oneOf(templateMock).search(with(any(String.class)), with(any(String.class)),
                        with(any(SearchControls.class)), with(any(AttributesMapper.class)));
            }
        });

        sut.groupExists("name exists");
        context.assertIsSatisfied();
    }

    /**
     * Tests looking up group with a domain.
     *
     * @throws NamingException
     *             shouldn't be thrown here.
     */
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

                oneOf(filterCreatorMock).getFilter("sAMAccountName=name exists");

                oneOf(templateMock).search(with(any(String.class)), with(any(String.class)),
                        with(any(SearchControls.class)), with(any(AttributesMapper.class)));
            }
        });

        sut.groupExists("domainname\\name exists");
        context.assertIsSatisfied();
    }
}
