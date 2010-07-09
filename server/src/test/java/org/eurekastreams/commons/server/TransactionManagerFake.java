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
package org.eurekastreams.commons.server;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * Fake class for transactional testing.
 *
 */
public class TransactionManagerFake implements PlatformTransactionManager
{
    /**
     * Fake method for testing.
     * @param def - Transaction Definition
     * @return TransactionStatus - fake.
     */
    public TransactionStatus getTransaction(final TransactionDefinition def)
    {
        return null;
    }

    /**
     * Fake stub method.
     * @param arg0 - fake.
     */
    public void commit(final TransactionStatus arg0)
    {
        // left blank.
    }

    /**
     * Fake stub method.
     * @param arg0 - fake.
     */
    public void rollback(final TransactionStatus arg0)
    {
        // left blank.
    }
}
