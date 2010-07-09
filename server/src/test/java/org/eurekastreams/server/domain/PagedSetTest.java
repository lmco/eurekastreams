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
/**
 *
 */
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * test paged sets.
 */
public class PagedSetTest
{

    /**
     * for error reporting.
     */
    private String message;

    /**
     * @throws java.lang.Exception
     *             for exceptions.
     */
    @Before
    public void setUp() throws Exception
    {
        message = null;
    }

    /**
     * @throws java.lang.Exception
     *             for exceptions.
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * test attributes for pagedset.
     */
    @Test
    public void testPagedSet()
    {
        message = "paged set properties should get correctly";

        ArrayList<Object> results = new ArrayList<Object>();
        ArrayList<Object> objResults = new ArrayList<Object>();

        int start = 0;
        int end = 1;
        int total = 3;
        PagedSet<Object> set = new PagedSet<Object>(start, end, total, results);
        assertEquals(message, start, set.getFromIndex());
        assertEquals(message, end, set.getToIndex());
        assertEquals(message, total, set.getTotal());
        assertEquals(message, results, set.getPagedSet());

        PagedSet<Object> setNeeds = new PagedSet<Object>(start, end, total, objResults);
        assertEquals(message, start, setNeeds.getFromIndex());
        assertEquals(message, end, setNeeds.getToIndex());
        assertEquals(message, total, setNeeds.getTotal());
        assertEquals(message, results, setNeeds.getPagedSet());
    }

    /**
     * test attributes for pagedset.
     */
    @Test
    public void testIsRangeValid()
    {
        message = "paged set should determine rangevalidity correctly";

        PagedSet<Object> set = new PagedSet<Object>();

        assertTrue(message, set.isRangeValid(0, 1));

        assertFalse(message, set.isRangeValid(1, 0));
        assertFalse(message, set.isRangeValid(-1, 0));
        assertFalse(message, set.isRangeValid(0, -1));

    }

    /**
     * test attributes for pagedset setters.
     */
    @Test
    public void testPagedSetSetters()
    {
        message = "paged set properties should set correctly";

        ArrayList<Object> results = new ArrayList<Object>();

        int start = 0;
        int end = 1;
        int total = 3;
        String elapsedTime = "0.323 seconds";
        PagedSet<Object> set = new PagedSet<Object>();
        set.setToIndex(end);
        set.setFromIndex(start);
        set.setTotal(total);
        set.setPagedSet(results);
        set.setElapsedTime(elapsedTime);
        assertEquals(message, start, set.getFromIndex());
        assertEquals(message, end, set.getToIndex());
        assertEquals(message, total, set.getTotal());
        assertEquals(message, results, set.getPagedSet());
        assertEquals(elapsedTime, set.getElapsedTime());
    }

    /**
     * test equals method.
     */
    @Test
    public void testPagedSetEquals()
    {
        message = "paged set should satisfy requirements on equals()";

        ArrayList<String> results = new ArrayList<String>();
        results.add("object");

        int start = 0;
        int end = 1;
        int total = 3;
        PagedSet<String> set = new PagedSet<String>(start, end, total, results);
        PagedSet<String> other = new PagedSet<String>();

        assertFalse(message, set.equals(null));
        assertFalse(message, set.equals("another class"));

        assertFalse(message, set.equals(other));

        other.setFromIndex(start);
        assertFalse(message, set.equals(other));

        other.setToIndex(end);
        assertFalse(message, set.equals(other));

        other.setTotal(total);
        assertFalse(message, set.equals(other));

        other.setPagedSet(results);
        assertTrue(message, set.equals(other));
    }

    /**
     * test hashcode.
     *
     * same object returns same hashcode
     *
     */
    @Test
    public void testPagedSetHashcode()
    {
        message = "paged set should satisfy requirements on hashcode()";

        ArrayList<String> results = new ArrayList<String>();
        results.add("object");

        int start = 0;
        int end = 1;
        int total = 3;
        PagedSet<String> set = new PagedSet<String>(start, end, total, results);
        PagedSet<String> other = new PagedSet<String>(start, end, total, results);

        // subsequent calls return same integer
        assertEquals(message, set.hashCode(), set.hashCode());

        // two object that are equal (by equals() method) return same hashcode
        assertEquals(message, set.hashCode(), other.hashCode());
    }

}
