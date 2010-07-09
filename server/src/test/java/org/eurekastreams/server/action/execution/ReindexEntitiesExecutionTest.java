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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.search.bootstrap.EntityReindexer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test suite for the {@link ReindexEntitiesExecution} class.
 * 
 */
public class ReindexEntitiesExecutionTest
{
    /**
     * Context for mocking.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked instance of the EntityReindexer.
     */
    private final EntityReindexer reindexerMock = context.mock(EntityReindexer.class);

    /**
     * Test execute().
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testExecute() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                one(reindexerMock).reindex();
            }
        });

        // SUT
        ReindexEntitiesExecution strategy = new ReindexEntitiesExecution(reindexerMock);
        strategy.execute(null);

        // assertions met?
        context.assertIsSatisfied();
    }

    /**
     * Test performAction() throws a RuntimeException when another thread is trying to do the same thing.
     */
    @Test
    public void testExecuteWhenAlreadyRunning()
    {
        // start running another instance of the action in a separate thread
        TestSleeperActionRunner sleeperActionRunner = new TestSleeperActionRunner();
        boolean exceptionThrown = false;
        try
        {
            sleeperActionRunner.start();

            // wait a split second to give the thread a chance of beating us
            final int maxIterations = 20; // 1 second
            int iteration = 0;
            while (!sleeperActionRunner.getDidStart())
            {
                // just in case:
                if (++iteration == maxIterations)
                {
                    throw new RuntimeException("Sleeper test action never reported that it was running.");
                }
                wait50ms();
            }
            // wait a tad bit longer because there's still a race condition
            wait50ms();

            // try running it for real, expecting a RuntimeException
            try
            {
                ReindexEntitiesExecution strategy = new ReindexEntitiesExecution(reindexerMock);
                strategy.execute(null);
            }
            catch (ExecutionException re)
            {
                if (re.getMessage().indexOf("Cannot perform a reindexing right now - it's already in progress.") > -1)
                {
                    exceptionThrown = true;
                }
            }
        }
        finally
        {
            // ask the sleeper action to kindly stop running
            sleeperActionRunner.stopRunning();
        }

        assertTrue("Expected RuntimeException to be thrown when attempting two concurrent reindexes.", exceptionThrown);
    }

    /**
     * Wait 50 milliseconds.
     */
    private void wait50ms()
    {
        try
        {
            final int sleepDuration = 50;
            Thread.sleep(sleepDuration);
        }
        catch (InterruptedException e)
        {
            log.error(e);
        }
    }

    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Test class that performs the EntityReindexerAction off in a separate thread, keeping it running until the thread
     * is killed.
     */
    private class TestSleeperActionRunner extends Thread
    {
        /**
         * Keep track if the fake reindexing is running.
         */
        private boolean didStart = false;

        /**
         * Whether we should keep running.
         */
        private boolean shouldKeepRunning = true;

        /**
         * Stop running.
         */
        public void stopRunning()
        {
            shouldKeepRunning = false;
        }

        /**
         * Return whether the fake indexing is running.
         * 
         * @return whether the fake indexing is running
         */
        public boolean getDidStart()
        {
            return didStart;
        }

        /**
         * Kick off the performAction method call in a separate thread.
         */
        @Override
        public void run()
        {
            log.debug("Thread started - running stalling performAction()");
            didStart = true;

            // kick off the fake indexing
            EntityReindexer reindexer = new EntityReindexerFake();
            ReindexEntitiesExecution strategy = new ReindexEntitiesExecution(reindexer);
            strategy.execute(null);
        }

        /**
         * Fake EntityReindexer - runs in a separate thread until it's told to stop.
         */
        private class EntityReindexerFake extends EntityReindexer
        {
            /**
             * faked-out method - will keep sleeping until dead.
             */
            @Override
            public void reindex()
            {
                final int maxIterations = 20; // 1 second
                int iteration = 0;
                while (shouldKeepRunning)
                {
                    // just in case:
                    if (++iteration == maxIterations)
                    {
                        throw new RuntimeException("Sleeper test action never told to stop running.");
                    }
                    wait50ms();
                }
            }
        }
    }

}
