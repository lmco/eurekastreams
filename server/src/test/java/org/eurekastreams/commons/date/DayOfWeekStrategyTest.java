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
package org.eurekastreams.commons.date;

import junit.framework.Assert;

import org.junit.Test;

import com.ibm.icu.util.Calendar;

/**
 * Test fixture for DayOfWeekStrategy.
 */
public class DayOfWeekStrategyTest
{
    /**
     * System under test.
     */
    private DayOfWeekStrategy sut = new DayOfWeekStrategy();

    /**
     * Test isWeekday on Sunday.
     */
    @Test
    public void testIsWeekdaySunday()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Assert.assertFalse(sut.isWeekday(cal.getTime()));
    }

    /**
     * Test isWeekday on Monday.
     */
    @Test
    public void testIsWeekdayMonday()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Assert.assertTrue(sut.isWeekday(cal.getTime()));
    }

    /**
     * Test isWeekday on Tuesday.
     */
    @Test
    public void testIsWeekdayTuesday()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        Assert.assertTrue(sut.isWeekday(cal.getTime()));
    }

    /**
     * Test isWeekday on Wednesday.
     */
    @Test
    public void testIsWeekdayWednesday()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        Assert.assertTrue(sut.isWeekday(cal.getTime()));
    }

    /**
     * Test isWeekday on Thursday.
     */
    @Test
    public void testIsWeekdayThursday()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        Assert.assertTrue(sut.isWeekday(cal.getTime()));
    }

    /**
     * Test isWeekday on Friday.
     */
    @Test
    public void testIsWeekdayFriday()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        Assert.assertTrue(sut.isWeekday(cal.getTime()));
    }

    /**
     * Test isWeekday on Saturday.
     */
    @Test
    public void testIsWeekdaySaturday()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        Assert.assertFalse(sut.isWeekday(cal.getTime()));
    }
}
