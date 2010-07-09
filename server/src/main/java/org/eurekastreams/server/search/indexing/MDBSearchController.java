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
package org.eurekastreams.server.search.indexing;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.search.backend.impl.jms.AbstractJMSHibernateSearchController;

/**
 * MDBSearchController - listens to a JMS queue to index updated entities.
 */
public class MDBSearchController extends AbstractJMSHibernateSearchController implements MessageListener
{
    /**
     * Entity Manager.
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
        this.entityManager = inEntityManager;
    }

    /**
     * Local log instance.
     */
    private static Log logger = LogFactory.getLog(MDBSearchController.class);

    /**
     * Clean the session if necessary.
     * 
     * @param inSession
     *            the session
     */
    @Override
    protected void cleanSessionIfNeeded(final Session inSession)
    {
        // no-op
    }

    /**
     * Log that we received the message.
     * 
     * @param inMessage
     *            the message captured
     */
    public void onMessage(final Message inMessage)
    {
        if (logger.isDebugEnabled())
        {
            try
            {
                logger.debug("Picked up message with JMS Message ID: " + inMessage.getJMSMessageID());
            }
            catch (JMSException e)
            {
                // do nothing
                int i = 0;
                i++;
            }
        }
        super.onMessage(inMessage);
    }

    /**
     * Get the session from the injected EntityManager.
     * 
     * @return the session from the EntityManager
     */
    @Override
    protected Session getSession()
    {
        return (Session) entityManager.getDelegate();
    }

}
