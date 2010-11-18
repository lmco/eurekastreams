/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;

/**
 * Abstract DomainEntityMapper, allows for single spot for EntityManager injection so all mappers don't need to
 * duplicate the template code.
 */
public class BaseDomainMapper
{
    /**
     * The logger.
     */
    private final Log log = LogFactory.getLog(BaseDomainMapper.class);

    /**
     * EntityManager to use for all ORM operations.
     */
    private EntityManager entityManager;

    /**
     * Set the entity manager to use for all ORM operations.
     *
     * @param inEntityManager
     *            the EntityManager to use for all ORM operations.
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager inEntityManager)
    {
        entityManager = inEntityManager;
        if (log.isDebugEnabled())
        {
            log.debug("set the entity manager to " + (entityManager == null ? "NULL" : entityManager.toString())
                    + " for object: " + this.toString());
        }
    }

    /**
     * @return The EntityManager.
     */
    protected EntityManager getEntityManager()
    {
        if (entityManager != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug("getting the entity manager named " + entityManager.toString() + " from object: "
                        + this.toString());
            }
        }
        else
        {
            log.warn("the entity manager for object: " + this.toString() + " is null");
        }
        return entityManager;
    }

    /**
     * Get the Hibernate session from the EntityManager.
     *
     * @return the Hibernate session from the EntityManager.
     */
    protected Session getHibernateSession()
    {
        return (Session) getEntityManager().getDelegate();
    }

    /**
     * Update all entities that have changed since they were loaded within the same context.
     */
    public void flush()
    {
        entityManager.flush();
    }

    /**
     * Clear the entity manager. Only use this if you know what you're doing. A good example is when you want to add an
     * item to an indexed collection, and get its proper indexColumn value back.
     */
    public void clear()
    {
        entityManager.clear();
    }

    /**
     * Build the PropertyProjection with alias.
     *
     * @param propertyName
     *            the property name
     * @return the PropertyProjection with alias
     */
    protected Projection getColumn(final String propertyName)
    {
        return Projections.property(propertyName).as(propertyName);
    }
}
