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

import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.exceptions.PrincipalPopulationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Test suite for the {@link PrincipalPopulatorTransWrapper}.
 *
 */
public class PrincipalPopulatorTransWrapperTest
{
    /**
     * System under test.
     */
    private PrincipalPopulatorTransWrapper sut;
    
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * The mock transaction manager.
     */
    private PlatformTransactionManager transactionManager = context.mock(PlatformTransactionManager.class);
    
    /**
     * The mocked instance of the {@link PrincipalPopulator} interface.
     */
    private PrincipalPopulator principalPopulator = context.mock(PrincipalPopulator.class);
    
    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new PrincipalPopulatorTransWrapper(principalPopulator, transactionManager);
    }
    
    /**
     * Test successful population.
     */
    @Test
    public void testSuccessfulPopulator()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                oneOf(principalPopulator).getPrincipal(with(any(String.class)));
                
                oneOf(transactionManager).commit(with(any(TransactionStatus.class)));
            }
        });
        
        sut.getPrincipal("testacct");
        context.assertIsSatisfied();
    }
    
    /**
     * Test the failure of retrieving a principal.
     */
    @Test(expected=PrincipalPopulationException.class)
    public void testUnsuccessfulPopulator()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));

                oneOf(principalPopulator).getPrincipal(with(any(String.class)));
                will(throwException(new PrincipalPopulationException()));
                
                oneOf(transactionManager).rollback(with(any(TransactionStatus.class)));
            }
        });
        sut.getPrincipal("testacct");
        context.assertIsSatisfied();
    }
}
