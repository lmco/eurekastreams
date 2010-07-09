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
package org.eurekastreams.commons.formatting;

import static junit.framework.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Test fixture for DateFormatter.
 */
public class DateFormatterTest
{
    /**
     * Test formatting.
     *
     * @throws ParseException
     *             Only if the test data is messed up.
     */
    @Test
    public void testFormatting() throws ParseException
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String, String> datesToTest = new HashMap<String, String>();
        datesToTest.put("2010-05-11 16:00:29", "Less than 1 minute ago");
        datesToTest.put("2010-05-11 15:59:31", "Less than 1 minute ago");

        datesToTest.put("2010-05-11 15:59:30", "1 minute ago");
        datesToTest.put("2010-05-11 15:58:31", "1 minute ago");
        datesToTest.put("2010-05-11 15:58:00", "2 minutes ago");
        datesToTest.put("2010-05-11 15:00:30", "1 hour ago");
        datesToTest.put("2010-05-11 14:00:31", "1 hour ago");
        datesToTest.put("2010-05-11 14:00:30", "2 hours ago");
        datesToTest.put("2010-05-11 00:00:00", "16 hours ago");

        // make sure yesterday means yesterday, not 24-48 hours ago (like the previous implemention)

        // test yesterday with all the edge cases
        datesToTest.put("2010-05-10 23:59:59", "Yesterday at 11:59pm");
        datesToTest.put("2010-05-10 16:00:31", "Yesterday at 4:00pm");
        datesToTest.put("2010-05-10 16:00:30", "Yesterday at 4:00pm");
        datesToTest.put("2010-05-10 16:00:29", "Yesterday at 4:00pm");
        datesToTest.put("2010-05-10 00:00:00", "Yesterday at 12:00am");

        // test the day before yesterday with all the edge cases
        datesToTest.put("2010-05-09 23:59:59", "Sunday at 11:59pm");
        datesToTest.put("2010-05-09 16:00:31", "Sunday at 4:00pm");
        datesToTest.put("2010-05-09 16:00:30", "Sunday at 4:00pm");
        datesToTest.put("2010-05-09 16:00:29", "Sunday at 4:00pm");
        datesToTest.put("2010-05-09 00:00:00", "Sunday at 12:00am");

        // test the am/pm logic
        datesToTest.put("2010-05-08 23:00:00", "Saturday at 11:00pm");
        datesToTest.put("2010-05-08 13:00:00", "Saturday at 1:00pm");
        datesToTest.put("2010-05-08 12:00:00", "Saturday at 12:00pm");
        datesToTest.put("2010-05-08 11:00:00", "Saturday at 11:00am");
        datesToTest.put("2010-05-08 01:00:00", "Saturday at 1:00am");
        datesToTest.put("2010-05-08 00:00:00", "Saturday at 12:00am");

        // test the minute logic
        datesToTest.put("2010-05-07 11:00:00", "Friday at 11:00am");
        datesToTest.put("2010-05-07 11:01:00", "Friday at 11:01am");
        datesToTest.put("2010-05-07 11:09:00", "Friday at 11:09am");
        datesToTest.put("2010-05-07 11:10:00", "Friday at 11:10am");

        datesToTest.put("2010-05-05 00:00:00", "Wednesday at 12:00am");

        // insure the same day of week for last week uses the date (with various edge cases)
        datesToTest.put("2010-05-04 23:59:59", "May 4 at 11:59pm");
        datesToTest.put("2010-05-04 16:00:31", "May 4 at 4:00pm");
        datesToTest.put("2010-05-04 16:00:30", "May 4 at 4:00pm");
        datesToTest.put("2010-05-04 16:00:29", "May 4 at 4:00pm");
        datesToTest.put("2010-05-04 00:00:00", "May 4 at 12:00am");

        // test year boundary
        datesToTest.put("2010-01-01 00:00:00", "Jan 1 at 12:00am");
        datesToTest.put("2009-12-31 23:59:59", "Dec 31, 2009 at 11:59pm");

        datesToTest.put("2009-01-01 00:00:00", "Jan 1, 2009 at 12:00am");
        datesToTest.put("2008-12-31 23:59:59", "Dec 31, 2008 at 11:59pm");

        Date baseDate = df.parse("2010-05-11 16:00:30");
        DateFormatter sut = new DateFormatter(baseDate);

        for (Map.Entry<String, String> testPair : datesToTest.entrySet())
        {
            String testDateString = testPair.getKey();
            String result = sut.timeAgo(df.parse(testDateString));
            assertEquals("Incorrect format for " + testDateString, testPair.getValue(), result);
        }
    }

    /**
     * Test formatting with brief output.
     * 
     * @throws ParseException
     *             Only if the test data is messed up.
     */
    @Test
    public void testFormattingBrief() throws ParseException
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String, String> datesToTest = new HashMap<String, String>();
        datesToTest.put("2010-05-11 16:00:29", "Less than 1 minute ago");
        datesToTest.put("2010-05-11 15:59:31", "Less than 1 minute ago");

        datesToTest.put("2010-05-11 15:59:30", "1 minute ago");
        datesToTest.put("2010-05-11 15:58:31", "1 minute ago");
        datesToTest.put("2010-05-11 15:58:00", "2 minutes ago");
        datesToTest.put("2010-05-11 15:00:30", "1 hour ago");
        datesToTest.put("2010-05-11 14:00:31", "1 hour ago");
        datesToTest.put("2010-05-11 14:00:30", "2 hours ago");
        datesToTest.put("2010-05-11 00:00:00", "16 hours ago");

        // test ones that are affected
        datesToTest.put("2010-05-10 16:00:30", "Yesterday");
        datesToTest.put("2010-05-09 16:00:30", "Sunday");
        datesToTest.put("2010-05-08 13:00:00", "Saturday");
        datesToTest.put("2010-05-07 11:10:00", "Friday");
        datesToTest.put("2010-05-04 16:00:30", "May 4");
        datesToTest.put("2010-01-01 00:00:00", "Jan 1");
        datesToTest.put("2009-12-31 23:59:59", "Dec 31, 2009");
        datesToTest.put("2009-01-01 00:00:00", "Jan 1, 2009");
        datesToTest.put("2008-12-31 23:59:59", "Dec 31, 2008");

        Date baseDate = df.parse("2010-05-11 16:00:30");
        DateFormatter sut = new DateFormatter(baseDate);

        for (Map.Entry<String, String> testPair : datesToTest.entrySet())
        {
            String testDateString = testPair.getKey();
            String result = sut.timeAgo(df.parse(testDateString), true);
            assertEquals("Incorrect format for " + testDateString, testPair.getValue(), result);
        }
    }

    /**
     * test the default constructor.
     */
    @Test
    public void testConstructor()
    {
        Date date = new Date();
        String result = new DateFormatter().timeAgo(date);
        assertEquals("Default constructor should initialize with current time.", "Less than 1 minute ago", result);
    }
}
