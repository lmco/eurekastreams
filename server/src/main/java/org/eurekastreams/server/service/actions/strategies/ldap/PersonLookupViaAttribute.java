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
     * Returns a list of people from on ldap lookup based on an attribute value provided by user. Ldap attribute to
     * value search results are base entirely on the {@link LdapLookup} DAO passed into this strategy.
     * 
     * @param inSearchString
     *            the value to be matched to an ldap attribue.
     * @param inResultsUpperBound
     *            Max number of results.
     * @return List of Person objects.
     */
    @Override
    public List<Person> findPeople(final String inSearchString, final int inResultsUpperBound)
    {
        // lookup users.
        List<Person> results = ldapLookup.execute(new LdapLookupRequest(inSearchString, inResultsUpperBound));

        // set sourcelist criteria on results.
        for (Person p : results)
        {
            p.getSourceList().add(inSearchString);
        }

        return results;
    }

}
