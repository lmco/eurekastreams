/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper to update the system admins.
 */
public class SetSystemAdminsDbMapper extends BaseArgDomainMapper<List<String>, Boolean>
{
    /**
     * Set the people with the input account ids as the system administrators.
     * 
     * @param inAccountIds
     *            account ids of the system administrators
     * @return true
     */
    @Override
    public Boolean execute(final List<String> inAccountIds)
    {
        // remove admins that aren't in the list
        getEntityManager().createQuery(
                "UPDATE Person SET isAdministrator=false WHERE isAdministrator=true "
                        + "AND accountId NOT IN (:accountIds)").setParameter("accountIds", inAccountIds)
                .executeUpdate();

        // set admins that are in the list
        getEntityManager().createQuery(
                "UPDATE Person SET isAdministrator=true WHERE isAdministrator=false AND accountId IN (:accountIds)")
                .setParameter("accountIds", inAccountIds).executeUpdate();

        return true;
    }

}
