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

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.service.actions.strategies.ldap.filters.FindByAttrib;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

/**
 * Tests for mapping LDAP record to Person.
 */
public class MembershipCriteriaToPersonMapperTest
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
    private MembershipCriteriaToPersonMapper sut;

    /**
     * Mock LDAP template.
     */
    private LdapTemplate ldapTemplateMock = context.mock(LdapTemplate.class, "template1");

    /**
     * Mocked {@link LdapPersonLookup} strategy.
     */
    private LdapPersonLookup ldapPersonLookupMock = context.mock(LdapPersonLookup.class);

    /**
     * Setup fixtures.
     */
    @Before
    public final void setUp()
    {
        HashMap<String, LdapTemplate> templates = new HashMap<String, LdapTemplate>();

        templates.put("Default", ldapTemplateMock);
        List<Filter> filterList = new ArrayList<Filter>();
        filterList.add(new EqualsFilter("objectclass", "group"));
        FindByAttrib findGroup = new FindByAttrib(filterList);

        sut = new MembershipCriteriaToPersonMapper(templates, "", null, ldapPersonLookupMock, findGroup);

        sut.setPeople(new HashMap<String, Person>());
        sut.setBaseLdapPath(new DistinguishedName("DC=myorg,DC=com"));
    }

    /**
     * Tests mapping a group.
     *
     * @throws NamingException
     *             shoulnd't be thrown here.
     */
    @Test
    public final void testMapFromContext() throws NamingException
    {
        final DirContextAdapter contextMock = context.mock(DirContextAdapter.class, "group");
        final LdapContextSource contextSource = context.mock(LdapContextSource.class);
        final String[] contextSourceUrls = { "ldaps://foo.bar.example.com:3269" };

        context.checking(new Expectations()
        {
            {
                oneOf(contextMock).getDn();
                will(returnValue(
                        new DistinguishedName("CN=blah.whatever.foo,CN=Users,DC=domainacct,DC=us,DC=example,DC=com")));

                // due to logging statements
                allowing(ldapTemplateMock).getContextSource();
                will(returnValue(contextSource));

                // due to logging statements
                allowing(contextSource).getUrls();
                will(returnValue(contextSourceUrls));

                oneOf(ldapPersonLookupMock).setDefaultLdapTemplate(with(any(LdapTemplate.class)));

                // verify that person search is called
                oneOf(ldapPersonLookupMock).findPeople(with(any(String.class)), with(any(Integer.class)));

                // verify that search for subgroups is called
                oneOf(ldapTemplateMock).search(with(any(String.class)), with(any(String.class)),
                        with(any(SearchControls.class)), with(any(ContextMapper.class)));
            }
        });

        sut.mapFromContext(contextMock);

        context.assertIsSatisfied();
    }

    /**
     * Tests mapping a group using default ldap param passed in constructor.
     *
     * @throws NamingException
     *             shoulnd't be thrown here.
     */
    @Test
    public final void testMapFromContextDefaultLdapTemplate() throws NamingException
    {
        // mimic setup but use different template passed in constructor
        HashMap<String, LdapTemplate> templates = new HashMap<String, LdapTemplate>();
        templates.put("Default", ldapTemplateMock);
        final LdapTemplate defaultldapTemplateMock = context.mock(LdapTemplate.class, "template2");
        List<Filter> filterList = new ArrayList<Filter>();
        filterList.add(new EqualsFilter("objectclass", "group"));
        FindByAttrib findGroup = new FindByAttrib(filterList);
        sut = new MembershipCriteriaToPersonMapper(templates, "", defaultldapTemplateMock, ldapPersonLookupMock,
                findGroup);
        sut.setPeople(new HashMap<String, Person>());
        sut.setBaseLdapPath(new DistinguishedName("DC=myorg,DC=com"));

        final DirContextAdapter contextMock = context.mock(DirContextAdapter.class, "group");
        final LdapContextSource contextSource = context.mock(LdapContextSource.class);
        final String[] contextSourceUrls = { "ldaps://foo.bar.example.com:3269" };

        context.checking(new Expectations()
        {
            {
                oneOf(contextMock).getDn();
                will(returnValue(
                        new DistinguishedName("CN=blah.whatever.foo,CN=Users,DC=domainacct,DC=us,DC=example,DC=com")));

                // due to logging statements
                allowing(defaultldapTemplateMock).getContextSource();
                will(returnValue(contextSource));

                // due to logging statements
                allowing(contextSource).getUrls();
                will(returnValue(contextSourceUrls));

                oneOf(ldapPersonLookupMock).setDefaultLdapTemplate(with(any(LdapTemplate.class)));

                // verify that person search is called
                oneOf(ldapPersonLookupMock).findPeople(with(any(String.class)), with(any(Integer.class)));

                // verify that search for subgroups is called
                oneOf(defaultldapTemplateMock).search(with(any(String.class)), with(any(String.class)),
                        with(any(SearchControls.class)), with(any(ContextMapper.class)));
            }
        });

        sut.mapFromContext(contextMock);

        context.assertIsSatisfied();
    }

    /**
     * Test member attribute setter/getter.
     */
    @Test
    public void setandGetMemberAttrib()
    {
        sut.setMemberAttrib("attribute value");
        assertEquals("property should be gotten", "attribute value", sut.getMemberAttrib());
    }

    /**
     * Test GroupObjectClassAttribValue setter/getter.
     */
    @Test
    public void setandGetGroupObjectClassAttribValue()
    {
        sut.setGroupObjectClassAttribValue("attribute value");
        assertEquals("property should be gotten", "attribute value", sut.getGroupObjectClassAttribValue());
    }

    /**
     * Test PersonObjectClassAttribValue setter/getter.
     */
    @Test
    public void setandGetPersonObjectClassAttribValue()
    {
        sut.setPersonObjectClassAttribValue("attribute value");
        assertEquals("property should be gotten", "attribute value", sut.getPersonObjectClassAttribValue());
    }

    /**
     * Test ObjectClassAttrib setter/getter.
     */
    @Test
    public void setandGetObjectClassAttrib()
    {
        sut.setObjectClassAttrib("attribute value");
        assertEquals("property should be gotten", "attribute value", sut.getObjectClassAttrib());
    }

}
