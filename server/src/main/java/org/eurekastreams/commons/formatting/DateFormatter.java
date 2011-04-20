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

import java.util.Date;

/**
 * Formatting utility that creates display dates. Since it needs to work on both the client and server which do not
 * share common date formatting routines (GWT can't use SimpleDateFormat and the server can't use GWT routines), it
 * implements it's own (for now).
 */
// TODO: Inject this with a date access strategy to allow it to use Java and GWT date routines. The strategy used on the
// server would also take account of the user's timezone (explicitly; handled automatically on client).
// TODO: Via the strategies, handle locales.
public class DateFormatter
{
    /** MILLISECONDS_PER_SECOND. */
    private static final int MILLISECONDS_PER_SECOND = 1000;

    /** MILLISECONDS_PER_MINUTE. */
    private static final int MILLISECONDS_PER_MINUTE = 60 * 1000;

    /** MILLISECONDS_PER_HOUR. */
    private static final int MILLISECONDS_PER_HOUR = 60 * 60 * 1000;

    /** MILLISECONDS_PER_DAY. */
    private static final int MILLISECONDS_PER_DAY = (24 * 60 * 60 * 1000);

    /** Names of week days. */
    private static final String[] DAY_OF_WEEK_NAMES = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday" };

    /** Names of months. */
    private static final String[] MONTH_NAMES = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
            "Nov", "Dec" };

    /** For converting Java years. */
    private static final int YEAR_BASE = 1900;

    /**
     * Go off the same Date instance so multiple calls to timeAgo is consistent.
     */
    private Date baseDate;

    /**
     * Constructor.
     */
    public DateFormatter()
    {
        this(new Date());
    }

    /**
     * Constructor.
     * 
     * @param inBaseDate
     *            the date to base the 'ago' on - usually should be set as new Date().
     */
    public DateFormatter(final Date inBaseDate)
    {
        baseDate = inBaseDate;
    }

    /**
     * Creates a formatted representation of a given date/time, using the supplied current date/time as a reference.
     * 
     * @param theDate
     *            The date to represent.
     * @param nowDate
     *            "Now" reference.
     * @param brief
     *            If a brief form at should be used.
     * @return Formatted date.
     */
    @SuppressWarnings("deprecation")
    public static String timeAgo(final Date theDate, final Date nowDate, final boolean brief)
    {
        StringBuffer sb = new StringBuffer("");
        long deltaMilliseconds = nowDate.getTime() - theDate.getTime();

        // check for yesterday
        // This trumps the "agos".
        if (deltaMilliseconds < 2 * MILLISECONDS_PER_DAY)
        {
            // CANNOT just use a delta! Need to actually make sure the day of the event is within the day prior to
            // today. Consider: if it is 8:30AM now, yesterday = everything from 8.5 to 32.5 hours ago. Using 24-48
            // hours ago means that some of the day before yesterday will be labeled as yesterday which is just plain
            // wrong.
            int dayOfWeekDelta = nowDate.getDay() - theDate.getDay();
            if (dayOfWeekDelta == 1 || dayOfWeekDelta == 1 - 7)
            {
                if (brief)
                {
                    return "Yesterday";
                }
                else
                {
                    sb.append("Yesterday at ");
                    appendTimeOfDay(sb, theDate);
                    return sb.toString();
                }
            }
        }

        if (deltaMilliseconds < MILLISECONDS_PER_MINUTE)
        {
            sb.append("Less than one minute ago");
        }
        else if (deltaMilliseconds < 2 * MILLISECONDS_PER_MINUTE)
        {
            sb.append("1 minute ago");
        }
        else if (deltaMilliseconds < MILLISECONDS_PER_HOUR)
        {
            sb.append(deltaMilliseconds / MILLISECONDS_PER_MINUTE);
            sb.append(" minutes ago");
        }
        else if (deltaMilliseconds < 2 * MILLISECONDS_PER_HOUR)
        {
            sb.append("1 hour ago");
        }
        else if (deltaMilliseconds < MILLISECONDS_PER_DAY)
        {
            sb.append(deltaMilliseconds / MILLISECONDS_PER_HOUR);
            sb.append(" hours ago");
        }
        else
        {
            // for older than yesterday

            // For the past week, use the day of week name + time. But cut it off such that if today is Tuesday, that
            // last Tuesday doesn't fall under this rule. That way there's no confusion about which Tuesday it's talking
            // about.
            if (deltaMilliseconds < 7 * MILLISECONDS_PER_DAY && nowDate.getDay() != theDate.getDay())
            {
                appendDayOfWeek(sb, theDate);
            }
            else
            {
                // For older dates, use the month/day + time, and add the year on the end if it's not from this year.
                appendDate(sb, theDate, theDate.getYear() != nowDate.getYear());
            }
            if (!brief)
            {
                sb.append(" at ");
                appendTimeOfDay(sb, theDate);
            }
        }

        return sb.toString();
    }

    /**
     * Appends the day of week to the string being built.
     * 
     * @param sb
     *            String builder.
     * @param theDate
     *            Date being formatted.
     */
    @SuppressWarnings("deprecation")
    private static void appendDayOfWeek(final StringBuffer sb, final Date theDate)
    {
        sb.append(DAY_OF_WEEK_NAMES[theDate.getDay()]);
    }

    /**
     * Appends the date to the string being built.
     * 
     * @param sb
     *            String builder.
     * @param theDate
     *            Date being formatted.
     * @param withYear
     *            If the year should be shown.
     */
    @SuppressWarnings("deprecation")
    private static void appendDate(final StringBuffer sb, final Date theDate, final boolean withYear)
    {
        sb.append(MONTH_NAMES[theDate.getMonth()]);
        sb.append(' ');
        sb.append(theDate.getDate());
        if (withYear)
        {
            sb.append(", ");
            sb.append(theDate.getYear() + YEAR_BASE);
        }
    }

    /**
     * Appends the time of day to the string being built.
     * 
     * @param sb
     *            String builder.
     * @param theDate
     *            Date being formatted.
     */
    @SuppressWarnings("deprecation")
    private static void appendTimeOfDay(final StringBuffer sb, final Date theDate)
    {
        final int halfDay = 12;

        int hour = theDate.getHours();
        boolean pm = false;
        if (hour == 0)
        {
            hour = halfDay;
        }
        else if (hour >= halfDay)
        {
            pm = true;
            if (hour > halfDay)
            {
                hour -= halfDay;
            }
        }
        sb.append(hour);
        sb.append(':');
        int minute = theDate.getMinutes();
        if (minute <= 9)
        {
            sb.append('0');
        }
        sb.append(minute);
        sb.append(pm ? "pm" : "am");
    }

    /**
     * Creates a formatted representation of a given date/time, using the supplied current date/time as a reference.
     * 
     * @param theDate
     *            The date to represent.
     * @return Formatted date.
     */
    public String timeAgo(final Date theDate)
    {
        return timeAgo(theDate, baseDate, false);
    }

    /**
     * Creates a formatted representation of a given date/time, using the supplied current date/time as a reference.
     * 
     * @param theDate
     *            The date to represent.
     * @param brief
     *            If a brief form at should be used.
     * @return Formatted date.
     */
    public String timeAgo(final Date theDate, final boolean brief)
    {
        return timeAgo(theDate, baseDate, brief);
    }
}
