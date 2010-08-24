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

import java.util.List;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.ldap.LdapLookup;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.eurekastreams.server.service.actions.strategies.PersonLookupStrategy;

/**
 * Find people via LDAP attribute.
 * 
 */
public class PersonLookupViaAttribute implements PersonLookupStrategy
{

    /**
     * {@link LdapLookup} to use.
     */
    private LdapLookup<Person> ldapLookup;

    /**
     * Constructor.
     * 
     * @param inLdapLookup
     *            {@link LdapLookup}.
     */
    public PersonLookupViaAttribute(final LdapLookup<Person> inLdapLookup)
    {
        ldapLookup = inLdapLookup;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<Person> findPeople(final String inSearchString, final int inResultsUpperBound)
    {
        return ldapLookup.execute(new LdapLookupRequest(inSearchString, inResultsUpperBound));
    }

}
