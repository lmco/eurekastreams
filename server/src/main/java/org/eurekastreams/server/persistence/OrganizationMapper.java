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
package org.eurekastreams.server.persistence;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.Organization;

/**
 * This class provides the mapper functionality for Organization entities.
 */
@Deprecated
public class OrganizationMapper extends DomainEntityMapper<Organization>
{
    /**
     * Constructor.
     * 
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public OrganizationMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Retrieve the name of the DomainEntity. This is to allow for the super class to identify the table within
     * hibernate.
     * 
     * @return The name of the domain entity.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "Organization";
    }

    /**
     * Find by name.
     * 
     * @param inName
     *            the name of the user to search for
     * @return the Org with the account name.
     */
    @SuppressWarnings("unchecked")
    public Organization findByShortName(final String inName)
    {
        Query q = getEntityManager().createQuery("from Organization where shortname = :inName").setParameter("inName",
                inName.toLowerCase());
        List results = q.getResultList();

        return (results.size() == 0) ? null : (Organization) results.get(0);
    }
}
