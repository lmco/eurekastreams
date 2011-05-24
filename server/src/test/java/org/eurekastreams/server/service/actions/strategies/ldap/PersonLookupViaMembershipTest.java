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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.ldap.LdapGroup;
import org.eurekastreams.server.persistence.mappers.ldap.LdapLookup;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.ldap.core.DistinguishedName;

/**
 * Test for PersonLookupViaMembership.
 */
@SuppressWarnings("unchecked")
public class PersonLookupViaMembershipTest
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
     * Finds group(s) by name.
     */
    private LdapLookup<LdapGroup> groupMapper = context.mock(LdapLookup.class, "groupMapper");

    /**
     * Finds group(s) by membership.
     */
    private LdapLookup<LdapGroup> subGroupMapper = context.mock(LdapLookup.class, "subGroupMapper");

    /**
     * Finds people by membership.
     */
    private LdapLookup<Person> directGroupMemberMapper = context.mock(LdapLookup.class, "directGroupMemberMapper");

    /**
     * Group using in test.
     */
    private LdapGroup group = context.mock(LdapGroup.class, "group");

    /**
     * {@link DistinguishedName}.
     */
    private DistinguishedName dn = new DistinguishedName();

    /**
     * Base DN for LDAP queries.
     */
    private String baseLdapPath = "dc=com";

    /**
     * Person used in test.
     */
    private Person person = context.mock(Person.class);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        PersonLookupViaMembership sut = new PersonLookupViaMembership(groupMapper, subGroupMapper,
                directGroupMemberMapper, baseLdapPath);

        final List<LdapGroup> topLevelGroups = new ArrayList<LdapGroup>(Arrays.asList(group));
        final List<LdapGroup> subGroups = new ArrayList<LdapGroup>();
        final List<Person> directMembers = new ArrayList<Person>(Arrays.asList(person));

        context.checking(new Expectations()
        {
            {
                allowing(groupMapper).execute(with(any(LdapLookupRequest.class)));
                will(returnValue(topLevelGroups));

                allowing(subGroupMapper).execute(with(any(LdapLookupRequest.class)));
                will(returnValue(subGroups));

                allowing(group).getDistinguishedName();
                will(returnValue(dn));

                allowing(group).setSourceList(with(any(List.class)));

                allowing(group).getSourceList();

                allowing(person).setSourceList(with(any(List.class)));

                allowing(directGroupMemberMapper).execute(with(any(LdapLookupRequest.class)));
                will(returnValue(directMembers));

                allowing(person).getAccountId();
                will(returnValue("acctId"));
            }
        });

        sut.findPeople("foo", 5);

        context.assertIsSatisfied();
    }
}
