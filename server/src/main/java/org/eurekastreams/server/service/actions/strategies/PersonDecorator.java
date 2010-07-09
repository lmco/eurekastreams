/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

/**
 * Provides a template method to ensure the next decorator in line gets called.
 */
public abstract class PersonDecorator
{
    /**
     * The decorator wrapped by this one.
     */
    private PersonDecorator nextDecorator = null;

    /**
     * Constructor.
     * 
     * @param next
     *            the decorator to be wrapped in this one.
     */
    public PersonDecorator(final PersonDecorator next)
    {
        nextDecorator = next;
    }

    /**
     * Template method to have this decorator do its thing, then call the next
     * in line.
     * 
     * @param person
     *            the person to be decorated
     * @throws Exception
     *             passing through exceptions thrown by performDecoration
     */
    public void decorate(final Person person) throws Exception
    {
        if (null != nextDecorator)
        {
            nextDecorator.decorate(person);
        }

        performDecoration(person);
    }

    /**
     * Abstract method to be provided by the concrete decorators.
     * 
     * @param person
     *            the person to be decorated
     * @throws Exception
     *             various decorators can have different things go wrong
     */
    protected abstract void performDecoration(Person person) throws Exception;
}
