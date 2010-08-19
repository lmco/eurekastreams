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
package org.eurekastreams.server.action.principal;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.exceptions.PrincipalPopulationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * This class is a wrapper for the {@link PrincipalPopulator} that wrappers the calls in a transaction.
 * 
 */
public class PrincipalPopulatorTransWrapper implements PrincipalPopulator
{
    /**
     * Instance of the {@link PrincipalPopulator}.
     */
    private final PrincipalPopulator populator;

    /**
     * Instance of the {@link PlatformTransactionManager}.
     */
    private final PlatformTransactionManager transManager;

    /**
     * Constructor.
     * 
     * @param inPrincipalPopulator
     *            - instance of the principal populator to be wrapped with a transaction.
     * @param inTransManager
     *            - instance of the {@link PlatformTransactionManager} to wrap the populator call with.
     */
    public PrincipalPopulatorTransWrapper(final PrincipalPopulator inPrincipalPopulator,
            final PlatformTransactionManager inTransManager)
    {
        populator = inPrincipalPopulator;
        transManager = inTransManager;
    }

    /**
     * {@inheritDoc}. Wrapped with a transaction.
     */
    @Override
    public Principal getPrincipal(final String inAccountId)
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setReadOnly(true);
        TransactionStatus currentStatus = transManager.getTransaction(transDef);
        Principal currentUserPrincipal = null;
        try
        {
            // Get Principal object for current user.
            currentUserPrincipal = populator.getPrincipal(inAccountId);
            transManager.commit(currentStatus);
        }
        catch (Exception ex)
        {
            transManager.rollback(currentStatus);
            throw new PrincipalPopulationException("Error occurred retrieving principal for supplied account id.", ex);
        }

        return currentUserPrincipal;
    }

}
