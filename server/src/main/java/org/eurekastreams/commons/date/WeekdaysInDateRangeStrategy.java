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

import java.util.Calendar;
import java.util.Date;

/**
 * Strategy to get the number of weekdays between two date ranges.
 */
public class WeekdaysInDateRangeStrategy
{
    /**
     * Number of ms in a day.
     */
    public static final long MS_IN_DAY = 86400000;

    /**
     * Get the number of weekdays between the start and end date, including the start date, excluding the end date.
     * 
     * @param startDate
     *            the start date - this date is included if weekday
     * @param endDate
     *            the end date - ignored in the count
     * @return the number of weekdays between the start and end date, including the start date, excluding the end date
     */
    public int getWeekdayCountBetweenDates(final Date startDate, final Date endDate)
    {
        Calendar cal;
        Date start, end;
        long startMs, endMs;

        // normalize start date
        start = DateDayExtractor.getStartOfDay(startDate);

        // get and normalize the day before the end date
        cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DATE, -1);
        end = DateDayExtractor.getStartOfDay(cal.getTime());

        startMs = start.getTime();
        endMs = end.getTime();

        int weekdayCount = 0;
        while (startMs <= endMs)
        {
            cal.setTimeInMillis(startMs);

            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
            {
                weekdayCount++;
            }
            startMs += MS_IN_DAY;
        }
        return weekdayCount;
    }
}
