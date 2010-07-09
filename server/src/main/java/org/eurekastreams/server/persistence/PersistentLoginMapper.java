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
import org.eurekastreams.server.domain.PersistentLogin;

/**
 * This class provides the mapper functionality for PersistentLogin entities.
 */
public class PersistentLoginMapper extends DomainEntityMapper<PersistentLogin>
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public PersistentLoginMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Returns name of Entity this mapper deals with.
     *
     * @return The name of Entity this mapper deals with.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "PersistentLogin";
    }

    /**
     * Find persistent login by accountId, return null if not present.
     *
     * @param accountId
     *            The accountId.
     * @return PersistentLogin for user if present or null if not.
     */
    @SuppressWarnings("unchecked")
    public PersistentLogin findByAccountId(final String accountId)
    {
        Query q = getEntityManager().createQuery("from PersistentLogin where accountId = :accountId").setParameter(
                "accountId", accountId.toLowerCase());
        List results = q.getResultList();

        return (results.size() == 0) ? null : (PersistentLogin) results.get(0);
    }

    /**
     * Delete all PersistentLogin entries for a user.
     *
     * @param accountId
     *            The accountId of the user.
     */
    public void deletePersistentLogin(final String accountId)
    {
        getEntityManager().createQuery("Delete from PersistentLogin where accountId = :accountId").setParameter(
                "accountId", accountId.toLowerCase()).executeUpdate();
    }

    /**
     * Creates PersistentLogin entry for user or updates the already present one
     * with current information.
     *
     * @param login
     *            The current PersistentLogin to be stored.
     */
    public void createOrUpdate(final PersistentLogin login)
    {
        PersistentLogin original = findByAccountId(login.getAccountId().toLowerCase());

        if (original == null)
        {
            insert(login);
        }
        else
        {
            original.setTokenValue(login.getTokenValue());
            original.setTokenExpirationDate(login.getTokenExpirationDate());
            flush();
        }
    }

}
