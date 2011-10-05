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

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for GetItemsByPointerIdsMapper.
 */
public class GetItemByPointerIdMapperTest
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

    /** Mapper to look up the id by the pointer id. */
    private final DomainMapper<String, Long> pointerToIdMapper = context.mock(DomainMapper.class,
            "pointerToIdMapper");

    /** Mapper to look up the item by id. */
    private final DomainMapper<Long, String> itemByIdMapper = context.mock(DomainMapper.class,
            "itemByIdMapper");

    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        DomainMapper<String, String> sut = new GetItemByPointerIdMapper(pointerToIdMapper, itemByIdMapper);

        final String pointerValue = "The Pointer";
        final Long idValue = 400L;
        final String result = "The Result";

        context.checking(new Expectations()
        {
            {
                oneOf(pointerToIdMapper).execute(pointerValue);
                will(returnValue(idValue));

                oneOf(itemByIdMapper).execute(idValue);
                will(returnValue(result));
            }
        });

        assertEquals(result, sut.execute(pointerValue));

        context.assertIsSatisfied();
    }
}
