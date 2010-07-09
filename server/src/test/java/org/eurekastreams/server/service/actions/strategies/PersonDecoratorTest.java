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
package org.eurekastreams.server.service.actions.strategies;

import org.eurekastreams.server.domain.Person;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for PortalPageDecorator.
 */
public class PersonDecoratorTest
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
     * Mocked person to decorate.
     */
    private Person person = context.mock(Person.class);

    /**
     * Subject under test.
     */
    private PersonDecorator sut = null;

    /**
     * The second decorator.
     */
    private PersonDecorator nextDecorator = context.mock(PersonDecorator.class);

    /**
     * Create the SUT.
     */
    @Before
    public void setup()
    {
        sut = new PersonDecoratorFake(nextDecorator);
    }

    /**
     * Test the template method.
     * @throws Exception should not happen
     */
    @Test
    public void decorate() throws Exception
    {
        // Set up expectations
        context.checking(new Expectations()
        {
            {
                oneOf(nextDecorator).decorate(person);
            }
        });

        sut.decorate(person);

        context.assertIsSatisfied();
    }
}
