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
package org.eurekastreams.server.search.stream;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for StreamViewActivityIdListPageFetcherFactory.
 */
public class StreamViewActivityIdListPageFetcherFactoryTest
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
     * Mocked CompositeStreamActivityIdsMapper.
     */
    private CompositeStreamActivityIdsMapper compositeStreamActivityIdsMapperMock = context
            .mock(CompositeStreamActivityIdsMapper.class);

    /**
     * System under test.
     */
    private StreamViewActivityIdListPageFetcherFactory sut = new StreamViewActivityIdListPageFetcherFactory();

    /**
     * Test buildPageFetcher.
     */
    @Test
    public void testBuildPageFetcher()
    {
        final Long compositeStreamViewId = 123L;
        final Long personId = 882L;
        final List<Long> allResults = new ArrayList<Long>();
        allResults.add(3L);
        allResults.add(4L);
        allResults.add(5L);
        allResults.add(6L);

        context.checking(new Expectations()
        {
            {
                oneOf(compositeStreamActivityIdsMapperMock).execute(compositeStreamViewId, personId);
                will(returnValue(allResults));
            }
        });

        sut.setCompositeStreamActivityIdsMapper(compositeStreamActivityIdsMapperMock);
        PageFetcher<Long> pageFetcher = sut.buildPageFetcher(compositeStreamViewId, personId);
        assertEquals(allResults, pageFetcher.fetchPage(0, Integer.MAX_VALUE));

        // now test with a sublist
        List<Long> sublist = new ArrayList<Long>();
        sublist.add(3L);
        sublist.add(4L);
        assertEquals(sublist, pageFetcher.fetchPage(0, 2));
        
        context.assertIsSatisfied();
    }
}
