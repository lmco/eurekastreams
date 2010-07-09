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
package org.eurekastreams.commons.hibernate;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.classic.Session;

/**
 * SQL Query optimizing class using Hibernate-specific tweaks. The JPA
 * EntityManager is injected in.
 */
public class QueryOptimizer
{
    /**
     * EntityManager to use to get to the Hibernate session.
     */
    private EntityManager entityManager;

    /**
     * Set the entity manager.
     *
     * @param newEntityManager
     *            the entity manager.
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager newEntityManager)
    {
        this.entityManager = newEntityManager;
    }

    /**
     * Determine the size of the input persisted collection without loading the
     * individual entities. This will not work on query results - only
     * persistent collections.
     *
     * @param collection
     *            The Collection to check the size of.
     *
     * @return the size of the collection determined without loading it.
     */
    public long determineCollectionSize(final Collection<?> collection)
    {
        if (entityManager == null)
        {
            throw new NullPointerException("EntityManager is null - needs to be injected.");
        }

        Session session = (Session) entityManager.getDelegate();

        return ((Long) session.createFilter(collection, "select count(*)").list().get(0)).longValue();
    }
}
