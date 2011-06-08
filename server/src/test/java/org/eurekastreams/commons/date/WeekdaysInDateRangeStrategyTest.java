/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test fixture for WeekdaysInDateRangeStrategy.
 */
public class WeekdaysInDateRangeStrategyTest
{
    /**
     * System under test.
     */
    private WeekdaysInDateRangeStrategy sut = new WeekdaysInDateRangeStrategy();

    /**
     * 2011.
     */
    private final int i2011 = 2011;

    /**
     * 12.
     */
    private final int i12 = 12;

    /**
     * 14.
     */
    private final int i14 = 14;

    /**
     * 17.
     */
    private final int i17 = 17;

    /**
     * 30.
     */
    private final int i30 = 30;

    /**
     * Test 1 - January 7th, 2011 - Jan 14th, 2011.
     */
    @Test
    public void test1()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(i2011, 0, 7, i12, i30);
        Date start = cal.getTime();

        cal.set(i2011, 0, i14);
        Date end = cal.getTime();

        Assert.assertEquals(5, sut.getWeekdayCountBetweenDates(start, end));
    }

    /**
     * Test 2 - January 7th, 2011 - Jan 7th, 2011.
     */
    @Test
    public void test2()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(i2011, 0, 7, i12, i30);
        Date start = cal.getTime();

        Date end = cal.getTime();

        Assert.assertEquals(0, sut.getWeekdayCountBetweenDates(start, end));
    }

    /**
     * Test 3 - January 8th, 2011 - Jan 9th, 2011.
     */
    @Test
    public void test3()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(i2011, 0, 8);
        Date start = cal.getTime();

        cal.set(i2011, 0, 9);
        Date end = cal.getTime();

        Assert.assertEquals(0, sut.getWeekdayCountBetweenDates(start, end));
    }

    /**
     * Test 4 - January 9th, 2011 - Jan 8th, 2011.
     */
    @Test
    public void test4()
    {
        Calendar cal = Calendar.getInstance();

        cal.set(i2011, 0, 9);
        Date start = cal.getTime();

        cal.set(i2011, 0, 8);
        Date end = cal.getTime();

        Assert.assertEquals(0, sut.getWeekdayCountBetweenDates(start, end));
    }

    /**
     * Test 4 - January 14th, 2011 - Jan 17th, 2011.
     */
    @Test
    public void test5()
    {
        Calendar cal = Calendar.getInstance();

        cal.set(i2011, 0, i14);
        Date start = cal.getTime();

        cal.set(i2011, 0, i17);
        Date end = cal.getTime();

        Assert.assertEquals(1, sut.getWeekdayCountBetweenDates(start, end));
    }

    /**
     * Test 6 - January 6th, 2011 - Jan 7th, 2011.
     */
    @Test
    public void test6()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(i2011, 0, 6);
        Date start = cal.getTime();

        cal.set(i2011, 0, 7);
        Date end = cal.getTime();

        Assert.assertEquals(1, sut.getWeekdayCountBetweenDates(start, end));
    }
}
