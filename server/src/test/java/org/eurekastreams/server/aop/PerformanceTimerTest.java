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
package org.eurekastreams.server.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for PerformanceTimer.
 * 
 */
public class PerformanceTimerTest
{
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
     * {@link ProceedingJoinPoint}.
     */
    private ProceedingJoinPoint pjp = context.mock(ProceedingJoinPoint.class);

    /**
     * {@link Signature};
     */
    private Signature signature = context.mock(Signature.class);

    /**
     * Call object.
     */
    private Object target = new Object();

    /**
     * System under test.
     */
    private PerformanceTimer sut = new PerformanceTimer();

    /**
     * Test.
     * 
     * @throws Throwable
     */
    @Test
    public void test() throws Throwable
    {
        final Object[] args = new Object[] { null };
        context.checking(new Expectations()
        {
            {
                allowing(pjp).getTarget();
                will(returnValue(target));

                allowing(pjp).toShortString();
                will(returnValue("shortString"));

                allowing(pjp).proceed();

                allowing(pjp).getArgs();
                will(returnValue(args));

                allowing(pjp).getSignature();
                will(returnValue(signature));

                allowing(signature).toShortString();
                will(returnValue("signatureShortString"));
            }
        });

        sut.profile(pjp);
        context.assertIsSatisfied();
    }
}
