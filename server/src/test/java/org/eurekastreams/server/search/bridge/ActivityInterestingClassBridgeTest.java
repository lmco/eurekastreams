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
package org.eurekastreams.server.search.bridge;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.search.bridge.strategies.ComputeInterestingnessOfActivityStrategy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the interestingness class bridge for activities.
 */
public class ActivityInterestingClassBridgeTest
{
    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock interestingness strategy.
     */
    private static ComputeInterestingnessOfActivityStrategy interstingnessStrategMocky;

    /**
     * System under test.
     */
    private static ActivityInterestingClassBridge sut;

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setUp()
    {
        interstingnessStrategMocky = CONTEXT.mock(ComputeInterestingnessOfActivityStrategy.class);

        ActivityInterestingClassBridge.setInterstingnessStrategy(interstingnessStrategMocky);
        
        sut = new ActivityInterestingClassBridge();
    }

    /**
     * Test the SUT.
     */
    @Test
    public void test()
    {
        final Activity activity = CONTEXT.mock(Activity.class);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(interstingnessStrategMocky).computeInterestingness(activity);
            }
        });

        sut.objectToString(activity);
    }
}
