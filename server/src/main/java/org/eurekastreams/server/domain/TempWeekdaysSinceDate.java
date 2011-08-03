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
package org.eurekastreams.server.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Model that's only used for helping generate metrics for the Discover page. Before generating the Discover Page lists,
 * this table is wiped clean, and the last 30-or-so days of data will be populated into this table. We can then generate
 * the stats in SQL rather than after querying, which requires a re-sort.
 */
@Entity
public class TempWeekdaysSinceDate
{
    /**
     * The unix timestamp of the date - at 12:00:00.000 AM in milliseconds.
     */
    @Id
    private long dateTimeStampInMilliseconds;

    /**
     * The number of weekdays since that date.
     */
    private long numberOfWeekdaysSinceDate;

    /**
     * Empty constructor for serialization.
     */
    public TempWeekdaysSinceDate()
    {
    }

    /**
     * Constructor.
     * 
     * @param inDateTimeStampInMilliseconds
     *            the unix timestamp of the date - at 12:00:00.000 AM in milliseconds.
     * @param inNumberOfWeekdaysSinceDate
     *            the number of weekdays since that date.
     */
    public TempWeekdaysSinceDate(final long inDateTimeStampInMilliseconds, final long inNumberOfWeekdaysSinceDate)
    {
        dateTimeStampInMilliseconds = inDateTimeStampInMilliseconds;
        numberOfWeekdaysSinceDate = inNumberOfWeekdaysSinceDate;
    }

    /**
     * The unix timestamp of the date - at 12:00:00.000 AM in milliseconds.
     * 
     * @return the unix timestamp of the date - at 12:00:00.000 AM in milliseconds.
     */
    public long getDateTimeStampInMilliseconds()
    {
        return dateTimeStampInMilliseconds;
    }

    /**
     * Set the unix timestamp of the date - at 12:00:00.000 AM in milliseconds.
     * 
     * @param inDateTimeStampInMilliseconds
     *            the unix timestamp of the date - at 12:00:00.000 AM in milliseconds.
     */
    public void setDateTimeStampInMilliseconds(final long inDateTimeStampInMilliseconds)
    {
        dateTimeStampInMilliseconds = inDateTimeStampInMilliseconds;
    }

    /**
     * Get the number of weekdays since that date.
     * 
     * @return the number of weekdays since that date
     */
    public long getNumberOfWeekdaysSinceDate()
    {
        return numberOfWeekdaysSinceDate;
    }

    /**
     * Set the number of weekdays since that date.
     * 
     * @param inNumberOfWeekdaysSinceDate
     *            the number of weekdays since that date.
     */
    public void setNumberOfWeekdaysSinceDate(final long inNumberOfWeekdaysSinceDate)
    {
        numberOfWeekdaysSinceDate = inNumberOfWeekdaysSinceDate;
    }

}
