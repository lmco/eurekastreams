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

import org.junit.Before;
import org.junit.Test;

/**
 * TestFixture for ListWrappingPageFetcher.
 */
public class ListWrappingPageFetcherTest
{
    /**
     * Sample list to wrap.
     */
    private List<Long> wrappedList = new ArrayList<Long>();

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        wrappedList.add(1L);
        wrappedList.add(2L);
        wrappedList.add(3L);
        wrappedList.add(4L);
        wrappedList.add(5L);
        wrappedList.add(6L);
        wrappedList.add(7L);
        wrappedList.add(8L);
        wrappedList.add(9L);
    }

    /**
     * Test fetching a page with valid from/pagesize.
     */
    @Test
    public void testFetchPage()
    {
        List<Long> expectedList = new ArrayList<Long>();
        expectedList.add(3L);
        expectedList.add(4L);
        expectedList.add(5L);

        ListWrappingPageFetcher sut = new ListWrappingPageFetcher(wrappedList);
        assertEquals(expectedList, sut.fetchPage(2, 3));
    }

    /**
     * Test fetchPage when asking for too much data.
     */
    @Test
    public void testFetchPagePastEnd()
    {
        final int bigPageSize = 3000;
        List<Long> expectedList = new ArrayList<Long>();
        expectedList.add(7L);
        expectedList.add(8L);
        expectedList.add(9L);

        ListWrappingPageFetcher sut = new ListWrappingPageFetcher(wrappedList);
        assertEquals(expectedList, sut.fetchPage(6, bigPageSize));
    }

    /**
     * Test fetchPage when asking for data outside the range of the list.
     */
    @Test
    public void testFetchPageWhenStartIndexTooBig()
    {
        final int bigStartingIndex = 3893;
        List<Long> expectedList = new ArrayList<Long>();
        ListWrappingPageFetcher sut = new ListWrappingPageFetcher(wrappedList);
        assertEquals(expectedList, sut.fetchPage(bigStartingIndex, 3));
    }
}
