/*
 * Copyright (c) 2010-2013 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.HashMap;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdatePersonResponse;
import org.eurekastreams.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Mapper to update the person in the DB with any additional properties found from ldap or updated last name.
 */
public class UpdatePersonMapper extends BaseArgDomainMapper<Person, UpdatePersonResponse>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Update the person in the DB.
     * 
     * @param ldapPerson
     *            {@link Person}.
     * @return {@link UpdatePersonResponse}.
     */
    @Override
    public UpdatePersonResponse execute(final Person ldapPerson)
    {
        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person where accountId = :accountId")
                .setParameter("accountId", ldapPerson.getAccountId()).getSingleResult();

        HashMap<String, String> ldapAdditionalProperties = ldapPerson.getAdditionalProperties();
        HashMap<String, String> dbAdditionalProperties = dbPerson.getAdditionalProperties();
        boolean wasPersonUpdated = false;
        boolean wasPersonDisplayNameUpdated = false;

        // Checks to see if last name in ldap matches what the db has, updating db if they don't match.
        if (!dbPerson.getLastName().equals(ldapPerson.getLastName()))
        {
            dbPerson.setLastName(ldapPerson.getLastName());
            wasPersonUpdated = true;
            wasPersonDisplayNameUpdated = true;
        }

        // Checks to see if company in ldap matches what the db has, updating db if they don't match.
        if ((dbPerson.getCompanyName() == null && ldapPerson.getCompanyName() != null)
                || (dbPerson.getCompanyName() != null
                // line wrap
                && !dbPerson.getCompanyName().equals(ldapPerson.getCompanyName())))
        {
            dbPerson.setCompanyName(ldapPerson.getCompanyName());
            wasPersonUpdated = true;
        }

        // Checks to see if the display name suffix has changed - (displayNameSuffix isn't nullable)
        log.debug("Checking if the displayNameSuffix changed");
        String newDisplayNameSuffix = "";
        if (ldapPerson.getDisplayNameSuffix() != null)
        {
            newDisplayNameSuffix = ldapPerson.getDisplayNameSuffix();
        }
        if (!dbPerson.getDisplayNameSuffix().equals(newDisplayNameSuffix))
        {
            // display name has changed
            log.debug("displayNameSuffix did change - new value: " + newDisplayNameSuffix);
            dbPerson.setDisplayNameSuffix(newDisplayNameSuffix);
            wasPersonUpdated = true;
            wasPersonDisplayNameUpdated = true;
        }

        // Looks for any additional properties defined for the person retrieved from ldap call.
        if (ldapAdditionalProperties != null && !ldapAdditionalProperties.isEmpty())
        {
            boolean changed = false;
            if (dbAdditionalProperties == null)
            {
                changed = true;
            }
            else
            {
                // determine if properties are different
                // first check for new or changed values
                for (String key : ldapAdditionalProperties.keySet())
                {
                    String value = ldapAdditionalProperties.get(key);
                    if (!value.equalsIgnoreCase(dbAdditionalProperties.get(key)))
                    {
                        changed = true;
                        break;
                    }
                }
                // then check for deleted values
                if (!changed)
                {
                    for (String key : dbAdditionalProperties.keySet())
                    {
                        if (!ldapAdditionalProperties.containsKey(key))
                        {
                            changed = true;
                            break;
                        }
                    }
                }
            }

            // Updates the db user, if necessary.
            if (changed)
            {
                dbPerson.setAdditionalProperties(ldapAdditionalProperties);
                wasPersonUpdated = true;
            }
        }
        // Finds if any previously set db properties are no longer necessary due to not being defined on ldap person.
        else if (dbAdditionalProperties != null)
        {
            dbPerson.setAdditionalProperties(null);
            wasPersonUpdated = true;
        }

        if (wasPersonUpdated)
        {
            getEntityManager().flush();
        }

        return new UpdatePersonResponse(dbPerson.getId(), wasPersonUpdated, wasPersonDisplayNameUpdated);
    }
}
