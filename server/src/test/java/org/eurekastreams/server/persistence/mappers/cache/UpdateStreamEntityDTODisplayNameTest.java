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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for UpdateStreamEntityDTODisplayName.
 */
public class UpdateStreamEntityDTODisplayNameTest
{
    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private UpdateStreamEntityDTODisplayName sut = new UpdateStreamEntityDTODisplayName();

    /**
     * Test execute.
     */
    @Test
    public void testExecuteWithNewDisplayname()
    {
        final String newDisplayName = "sldkfjsdlkfj";
        final StreamEntityDTO entity = context.mock(StreamEntityDTO.class);
        final Person person = context.mock(Person.class);

        context.checking(new Expectations()
        {
            {
                allowing(entity).getId();
                will(returnValue(2L));

                allowing(entity).getDisplayName();
                will(returnValue("foo"));

                allowing(person).getDisplayName();
                will(returnValue(newDisplayName));

                // the write
                oneOf(entity).setDisplayName(newDisplayName);
            }
        });

        sut.execute(entity, person);

        context.assertIsSatisfied();
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecuteWithSameDisplayName()
    {
        final String newDisplayName = "sldkfjsdlkfj";
        final StreamEntityDTO entity = context.mock(StreamEntityDTO.class);
        final Person person = context.mock(Person.class);

        context.checking(new Expectations()
        {
            {
                allowing(entity).getId();
                will(returnValue(2L));

                allowing(entity).getDisplayName();
                will(returnValue(newDisplayName));

                allowing(person).getDisplayName();
                will(returnValue(newDisplayName));
            }
        });

        sut.execute(entity, person);

        context.assertIsSatisfied();
    }
}
