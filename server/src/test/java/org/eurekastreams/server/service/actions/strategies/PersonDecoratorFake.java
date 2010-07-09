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

/**
 * Support class for PortalPageDecoratorTest.
 */
public class PersonDecoratorFake extends PersonDecorator
{
    /**
     * Wrapper constructor.
     *
     * @param dec
     *            wrapped decorator
     */
    public PersonDecoratorFake(final PersonDecorator dec)
    {
        super(dec);
    }

    /**
     * Provide some implementation so that the class can be instantiated.
     *
     * @param person
     *            not used
     */
    @Override
    protected void performDecoration(final Person person)
    {
        // empty
    }

}
