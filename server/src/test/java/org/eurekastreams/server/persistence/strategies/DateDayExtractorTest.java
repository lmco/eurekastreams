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
package org.eurekastreams.server.persistence.strategies;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.ibm.icu.util.Calendar;

/**
 * Test fixture for DateDayExtractor.
 */
public class DateDayExtractorTest
{
    /**
     * April 4th, 2011 in ticks.
     */
    private final long april4th2011 = 1301944331000L;

    /**
     * Test getStartOfDay().
     */
    @Test
    public void testGetStartOfDay()
    {
        final long year = 2011;
        final long month = 4 - 1;
        final long day = 4;

        Date date = DateDayExtractor.getStartOfDay(new Date(april4th2011));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        assertEquals(year, cal.get(Calendar.YEAR));
        assertEquals(month, cal.get(Calendar.MONTH));
        assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
    }

    /**
     * Test getStartOfDay().
     */
    @Test
    public void testGetEndOfDay()
    {
        final long year = 2011;
        final long month = 4 - 1;
        final long day = 4;
        final long twentyThree = 23;
        final long fiftyNine = 59;
        final int noNoNo = 999;

        Date date = DateDayExtractor.getEndOfDay(new Date(april4th2011));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        assertEquals(year, cal.get(Calendar.YEAR));
        assertEquals(month, cal.get(Calendar.MONTH));
        assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(twentyThree, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(fiftyNine, cal.get(Calendar.MINUTE));
        assertEquals(fiftyNine, cal.get(Calendar.SECOND));
        assertEquals(noNoNo, cal.get(Calendar.MILLISECOND));
    }
}
