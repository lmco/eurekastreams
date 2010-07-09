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
package org.eurekastreams.commons.scheduling;

import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * test TransactionalTaskRunner.
 */
public class TransactionalTaskRunnerTest
{
    /**
     * Used for mocking classes in this test.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };


    /**
     * default data for the test.
     */
    @SuppressWarnings("unchecked")
    private Set target = context.mock(Set.class);

    /**
     * Subject under test.
     */
    private TransactionalTaskRunner taskRunner;

    /**
     * @throws java.lang.Exception
     *             for exceptions.
     */
    @Before
    public void setUp() throws Exception
    {
        // pretend Set.size() contains JPA code
        // which is required to be called from a @Transactional method
        // (doesn't have to, but that's the point of TransactionalTaskRunner)
        taskRunner = new TransactionalTaskRunner(target, "size");
    }

    /**
     * @throws java.lang.Exception
     *             for exceptions.
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Just make sure that the method you specified is the one that gets called.
     */
    @Test
    public void testRunScheduledTask()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(target).size();
            }
        });

        taskRunner.runTransactionalTask();

        context.assertIsSatisfied();

    }

}
