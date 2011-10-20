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
package org.eurekastreams.server.persistence.mappers;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Decorates a DAO (mapper) to call it in a transaction.
 *
 * @param <RQ>
 *            Request type for the DAO.
 * @param <RS>
 *            Result type for the DAO.
 */
public class MapperTransactionDecorator<RQ, RS> implements DomainMapper<RQ, RS>
{
    /** The decorated DAO. */
    private final DomainMapper<RQ, RS> dao;

    /** Instance of the {@link PlatformTransactionManager}. */
    private final PlatformTransactionManager transManager;

    /**
     * Constructor.
     *
     * @param inDao
     *            The decorated DAO.
     * @param inTransManager
     *            Transaction manager.
     */
    public MapperTransactionDecorator(final DomainMapper<RQ, RS> inDao, final PlatformTransactionManager inTransManager)
    {
        dao = inDao;
        transManager = inTransManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RS execute(final RQ inRequest)
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setReadOnly(true);
        TransactionStatus currentStatus = transManager.getTransaction(transDef);
        try
        {
            RS result = dao.execute(inRequest);
            transManager.commit(currentStatus);
            return result;
        }
        // Note: Neither execute() nor commit() throw any non-RuntimeExceptions, so catching RuntimeException covers all
        // possible cases.
        catch (RuntimeException ex)
        {
            transManager.rollback(currentStatus);
            throw ex;
        }
    }
}
