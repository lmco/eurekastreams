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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.HashMap;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdatePersonResponse;

/**
 * Mapper to update the person in the DB with any additional properties found from ldap.
 * 
 */
public class UpdatePersonMapper extends BaseArgDomainMapper<Person, UpdatePersonResponse>
{
    /**
     * Update the person in the DB.
     *
     * @param ldapPerson
     *                    {@link Person}.
     * @return {@link UpdatePersonResponse}.
     */
    @Override
    public UpdatePersonResponse execute(final Person ldapPerson)
    {
        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person where accountId = :accountId")
                .setParameter("accountId", ldapPerson.getAccountId()).getSingleResult();

        HashMap<String, String> ldapAdditionalProperties = ldapPerson.getAdditionalProperties();
        HashMap<String, String> dbAdditionalProperties = dbPerson.getAdditionalProperties();
        HashMap<String, String> updatedProperties = new HashMap<String, String>();
        boolean wasPersonUpdated = false;

        // Looks for any additional properties defined for the person retrieved from ldap call.
        if (ldapAdditionalProperties != null)
        {
            for (String key : ldapAdditionalProperties.keySet())
            {
                String value = ldapAdditionalProperties.get(key);
                // Finds if properties need to be updated in the db copy of the user. 
                if (dbAdditionalProperties == null || !value.equalsIgnoreCase(dbAdditionalProperties.get(key)))
                {
                    updatedProperties.put(key, value);
                }
            }

            // Updates the db user, if necessary.
            if (updatedProperties.keySet().size() > 0)
            {
                dbPerson.setAdditionalProperties(updatedProperties);
                getEntityManager().createQuery("UPDATE Person SET additionalProperties = :props where id = :id")
                        .setParameter("id", dbPerson.getId()).setParameter("props", updatedProperties).executeUpdate();
                wasPersonUpdated = true;
            }
        }
        // Finds if any previously set db properties are no longer necessary due to not being defined on ldap person.
        else if (dbAdditionalProperties != null)
        {
            getEntityManager().createQuery("UPDATE Person SET additionalProperties = null where id = :id")
                    .setParameter("id", dbPerson.getId()).executeUpdate();
            wasPersonUpdated = true;
        }

        return new UpdatePersonResponse(dbPerson, wasPersonUpdated);
    }
}

