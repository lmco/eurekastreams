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
import org.eurekastreams.server.domain.OAuthDomainEntry;

/**
 * This class provides the mapper functionality for OAuthDomainEntry entities.
 */
public class OAuthEntryMapper extends DomainEntityMapper<OAuthDomainEntry>
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public OAuthEntryMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Get the domain entity name for the generic query operations.
     *
     * @return the domain entity name for the gadget query operations.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "OAuthDomainEntry";
    }

    /**
     * Find a specific entry.
     *
     * @param inToken
     *            Token for the OAuthEntry.
     *
     * @return the entry that matches the parameters (if any).
     */
    @SuppressWarnings("unchecked")
    public OAuthDomainEntry findEntry(final String inToken)
    {
        Query q = getEntityManager().createQuery("from OAuthDomainEntry e where e.token = :token").setParameter("token",
                inToken);

        List results = q.getResultList();
        if (results.size() == 0)
        {
            return null;
        }
        OAuthDomainEntry entry = (OAuthDomainEntry) results.get(0);
        return entry;
    }

    /**
     * Delete an entry.
     *
     * @param inEntryId
     *            The id of the entry to delete.
     */
    public void delete(final long inEntryId)
    {
        OAuthDomainEntry entry = findById(inEntryId);
        if (entry != null)
        {
            getEntityManager().remove(entry);
        }
    }

    /**
     * Delete an entry.
     *
     * @param inToken
     *            The token of the entry to delete.
     */
    public void delete(final String inToken)
    {
        OAuthDomainEntry entry = findEntry(inToken);
        if (entry != null)
        {
            delete(entry.getId());
        }
    }
}
