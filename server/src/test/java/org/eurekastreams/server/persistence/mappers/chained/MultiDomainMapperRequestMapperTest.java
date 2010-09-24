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
package org.eurekastreams.server.persistence.mappers.chained;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for the mulitdomian mapper.
 *
 */
public class MultiDomainMapperRequestMapperTest
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
     * System under test.
     */
    private MultiDomainMapperRequestMapper<Long, Long> sut;

    /**
     * Mapper mock.
     */
    private DomainMapper<Long, Long> mapper = context.mock(DomainMapper.class);

    /**
     * Test.
     */
    @Test
    public void execute()
    {
        final List<Long> requests = new ArrayList<Long>();
        requests.add(1L);
        requests.add(2L);

        sut = new MultiDomainMapperRequestMapper(mapper);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(1L);
                will(returnValue(3L));

                oneOf(mapper).execute(2L);
                will(returnValue(4L));
            }
        });

        List<Long> responses = sut.execute(requests);

        assertSame(3L, responses.get(0));
        assertSame(4L, responses.get(1));

        context.assertIsSatisfied();
    }
}
