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
package org.eurekastreams.web.client.ui;

import java.util.Date;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.ui.TimerFactory.EventedTimer;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;

/**
 * Tests {@link PeriodicEventManager}.
 */
public class PeriodicEventManagerTest
{
    /** Test data. */
    private static final int IDLE_TIMEOUT = 15;

    /** Obvious to everyone but checkstyle. */
    private static final long MS_PER_S = 1000;

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            GWTMockUtilities.disarm();
        }
    };

    /** Fixture: timer factory. */
    private TimerFactory timerFactory = context.mock(TimerFactory.class);

    /** Fixture: timer. */
    private EventedTimer timer = context.mock(EventedTimer.class);

    /** Fixture: action processor. */
    private ActionProcessor actionProcessor = context.mock(ActionProcessor.class);

    /** Fixture: registered action. */
    private TimerHandler action1 = context.mock(TimerHandler.class, "action1");

    /** Fixture: registered action. */
    private TimerHandler action2 = context.mock(TimerHandler.class, "action2");

    /** Interceptor. */
    private AnonymousClassInterceptor<TimerHandler> timerHandlerInt;

    /** SUT. */
    private PeriodicEventManager sut;

    /** Time to use for "now". */
    private Date now;

    /**
     * Simulates time passing by one second.
     */
    private void incrementTime()
    {
        now = new Date(now.getTime() + MS_PER_S);
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        now = new Date();
        timerHandlerInt = new AnonymousClassInterceptor<TimerHandler>();

        context.checking(new Expectations()
        {
            {
                oneOf(timerFactory).createTimer(with(any(TimerHandler.class)));
                will(doAll(timerHandlerInt, returnValue(timer)));
            }
        });

        sut = new PeriodicEventManager(IDLE_TIMEOUT, timerFactory, actionProcessor)
        {
            @Override
            protected Date timeNow()
            {
                return now;
            }
        };
    }

    /**
     * Starts the timer.
     */
    private void setupToRun()
    {
        final States state = context.states("setupSegregator").startsAs("setup");
        context.checking(new Expectations()
        {
            {
                oneOf(timer).scheduleRepeating(with(any(Integer.class)));
                when(state.is("setup"));
            }
        });
        sut.start();
        state.become("");

incrementTime();
    }

    /**
     * Tests registration (by seeing handler invoked on timer).
     */
    @Test
    public void testRegister()
    {
        sut.register(action1, 0);

        setupToRun();

        context.checking(new Expectations()
        {
            {
                oneOf(action1).run();

                oneOf(actionProcessor).setQueueRequests(true);
                oneOf(actionProcessor).fireQueuedRequests();
                oneOf(actionProcessor).setQueueRequests(false);
            }
        });

        timerHandlerInt.getObject().run();

        context.assertIsSatisfied();
    }

    /**
     * Tests deregistration (by seeing handler not invoked on timer).
     */
    @Test
    public void testDeregister()
    {
        sut.register(action1, 0);
        sut.register(action2, 0);

        setupToRun();

        sut.deregister(action1);
        sut.deregister(action2);

        timerHandlerInt.getObject().run();

        context.assertIsSatisfied();
    }

    /**
     * Tests timing of periodic actions.
     */
    @Test
    public void testTiming()
    {
        setupToRun();

        // At this point, time = 1
        // So action1 should run at: 3, 5, 7, etc.
        // So action2 should run at: 4, 7, 10
        sut.register(action1, 2);
        sut.register(action2, 3);

        TimerHandler hdlr = timerHandlerInt.getObject();

        final States state = context.states("time").startsAs("1");
        context.checking(new Expectations()
        {
            {
                oneOf(action1).run();
                when(state.is("3"));
                oneOf(action1).run();
                when(state.is("5"));
                oneOf(action1).run();
                when(state.is("7"));
                oneOf(action1).run();
                when(state.is("9"));
                oneOf(action1).run();
                when(state.is("11"));

                oneOf(action2).run();
                when(state.is("4"));
                oneOf(action2).run();
                when(state.is("7"));
                oneOf(action2).run();
                when(state.is("10"));

                allowing(actionProcessor).setQueueRequests(true);
                allowing(actionProcessor).fireQueuedRequests();
                allowing(actionProcessor).setQueueRequests(false);
            }
        });

        for (int i = 2; i <= 9 + 3; i++)
        {
            incrementTime();
            state.become(Integer.toString(i));
            hdlr.run();
        }

        context.assertIsSatisfied();
    }

    /**
     * Tests system becoming idle.
     */
    @Test
    public void testBecomingIdle()
    {
        setupToRun();

        // At this point, time = 1
        // So action1 should run at: 1, 2, 3, etc.
        // Will become idle at time = 15
        sut.register(action1, 0);

        TimerHandler hdlr = timerHandlerInt.getObject();

        final States state = context.states("time").startsAs("notLast");
        context.checking(new Expectations()
        {
            {
                exactly(IDLE_TIMEOUT - 1).of(action1).run();
                when(state.is("notLast"));

                oneOf(timer).cancel();
                when(state.is(StaticResourceBundle.INSTANCE.coreCss().last()));

                allowing(actionProcessor).setQueueRequests(true);
                allowing(actionProcessor).fireQueuedRequests();
                allowing(actionProcessor).setQueueRequests(false);
            }
        });

        for (int i = 1; i <= IDLE_TIMEOUT; i++)
        {
            if (i == IDLE_TIMEOUT)
            {
                state.become(StaticResourceBundle.INSTANCE.coreCss().last());
            }
            hdlr.run();

            incrementTime();
        }

        context.assertIsSatisfied();
    }


    /**
     * Tests activity preventing idleness.
     */
    @Test
    public void testActivityPreventIdle()
    {
        setupToRun();

        final int activityAtTime = 3;

        // At this point, time = 1
        // Will become idle at time = 15, except activty at 3 will bump it to 18

        TimerHandler hdlr = timerHandlerInt.getObject();

        final States state = context.states("time").startsAs("notLast");
        context.checking(new Expectations()
        {
            {
                oneOf(timer).cancel();
                when(state.is(StaticResourceBundle.INSTANCE.coreCss().last()));
            }
        });

        for (int i = 1; i <= IDLE_TIMEOUT + activityAtTime; i++)
        {
            if (i == IDLE_TIMEOUT + activityAtTime)
            {
                state.become(StaticResourceBundle.INSTANCE.coreCss().last());
            }
            if (i == activityAtTime)
            {
                sut.userActivityDetected();
            }
            hdlr.run();

            incrementTime();
        }

        context.assertIsSatisfied();
    }

    /**
     * Tests activity restarting from idleness.
     */
    @Test
    public void testActivityRestartFromIdle()
    {
        setupToRun();

        TimerHandler hdlr = timerHandlerInt.getObject();

        // At this point, time = 1
        // Will become idle at time = 15
        final States state = context.states("time").startsAs("makeIdle");
        context.checking(new Expectations()
        {
            {
                oneOf(timer).cancel();
                when(state.is("makeIdle"));
            }
        });
        for (int i = 1; i <= IDLE_TIMEOUT; i++)
        {
            if (i == IDLE_TIMEOUT)
            {
                state.become("makeIdle");
            }
            hdlr.run();

            incrementTime();
        }

        // Now time = 16 and system is idle
        context.checking(new Expectations()
        {
            {
                oneOf(timer).scheduleRepeating(with(any(Integer.class)));
            }
        });
        sut.userActivityDetected();

        context.assertIsSatisfied();
    }

}
