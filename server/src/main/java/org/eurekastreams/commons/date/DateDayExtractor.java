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
 * Helper class to extract a date at the start and end of a date.
 */
public final class DateDayExtractor
{
    /**
     * Private constructor because this is a utility class.
     */
    private DateDayExtractor()
    {
    }

    /**
     * Get a date that's 12:00:00am from the input date.
     * 
     * @param inDate
     *            the date to get the start of day from
     * @return the input date with the time set to 12:00:00.000 AM
     */
    public static Date getStartOfDay(final Date inDate)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Get a date that's 11:59:00.999PM from the input date.
     * 
     * @param inDate
     *            the date to get the start of day from
     * @return the input date with the time set to 11:59:00.999PM
     */
    public static Date getEndOfDay(final Date inDate)
    {
        final int elevenPmHours = 23;
        final int fiftyNineMinutes = 59;
        final int noNoNo = 999;

        Calendar cal = Calendar.getInstance();
        cal.setTime(inDate);
        cal.set(Calendar.HOUR_OF_DAY, elevenPmHours);
        cal.set(Calendar.MINUTE, fiftyNineMinutes);
        cal.set(Calendar.SECOND, fiftyNineMinutes);
        cal.set(Calendar.MILLISECOND, noNoNo);
        return cal.getTime();
    }
}
