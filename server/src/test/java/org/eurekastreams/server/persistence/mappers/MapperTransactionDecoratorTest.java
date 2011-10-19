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

import static org.junit.Assert.assertSame;

import java.io.Serializable;

import org.eurekastreams.commons.exceptions.ExecutionException;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Test suite for the {@link MapperTransactionDecorator}.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapperTransactionDecoratorTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: dao. */
    private final DomainMapper dao = mockery.mock(DomainMapper.class, "dao");

    /** Fixture: transaction manager. */
    private final PlatformTransactionManager transactionManager = mockery.mock(PlatformTransactionManager.class);

    /** Fixture: request. */
    private final Serializable request = mockery.mock(Serializable.class, "request");

    /** Fixture: response. */
    private final Serializable response = mockery.mock(Serializable.class, "response");

    /** Fixture: transaction. */
    private final TransactionStatus transaction = mockery.mock(TransactionStatus.class, "transaction");

    /** System under test. */
    private MapperTransactionDecorator sut;


    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new MapperTransactionDecorator(dao, transactionManager);
    }

    /**
     * Test successful execution.
     */
    @Test
    public void testSuccess()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));
                will(returnValue(transaction));

                oneOf(dao).execute(with(same(request)));
                will(returnValue(response));

                oneOf(transactionManager).commit(with(same(transaction)));
            }
        });

        Object result = sut.execute(request);
        mockery.assertIsSatisfied();
        assertSame(response, result);
    }

    /**
     * Test failed execution.
     */
    @Test(expected = ExecutionException.class)
    public void testFailure()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(transactionManager).getTransaction(with(any(DefaultTransactionDefinition.class)));
                will(returnValue(transaction));

                oneOf(dao).execute(with(same(request)));
                will(throwException(new ExecutionException("BAD")));

                oneOf(transactionManager).rollback(with(same(transaction)));
            }
        });
        sut.execute(request);
        mockery.assertIsSatisfied();
    }
}
