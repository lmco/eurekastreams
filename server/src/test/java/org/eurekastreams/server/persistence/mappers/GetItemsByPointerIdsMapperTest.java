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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for GetItemsByPointerIdsMapper.
 */
public class GetItemsByPointerIdsMapperTest
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
     * Mapper to look up the ids by the pointer ids.
     */
    private DomainMapper<List<String>, List<Long>> pointersToIdMapper = context.mock(DomainMapper.class,
            "pointersToIdMapper");

    /**
     * Mapper to look up the item by id.
     */
    private DomainMapper<List<Long>, List<String>> itemsByIdMapper = context
            .mock(DomainMapper.class, "itemsByIdMapper");

    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        GetItemsByPointerIdsMapper sut = new GetItemsByPointerIdsMapper(pointersToIdMapper, itemsByIdMapper);

        final List<String> pointerList = new ArrayList<String>();
        final List<Long> ids = context.mock(List.class);
        final List<String> results = new ArrayList<String>();

        context.checking(new Expectations()
        {
            {
                oneOf(pointersToIdMapper).execute(pointerList);
                will(returnValue(ids));

                oneOf(itemsByIdMapper).execute(ids);
                will(returnValue(results));
            }
        });

        assertSame(results, sut.execute(pointerList));

        context.assertIsSatisfied();
    }
}
