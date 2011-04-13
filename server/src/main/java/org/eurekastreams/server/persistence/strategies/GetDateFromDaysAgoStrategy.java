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

import java.util.Date;

import com.ibm.icu.util.Calendar;

/**
 * Strategy to get a date from N days ago.
 */
public class GetDateFromDaysAgoStrategy
{
    /**
     * Return a date from daysAgo days ago.
     * 
     * @param daysAgo
     *            the number of days ago to return a day from
     * @return a date from daysAgo days ago.
     */
    public Date execute(final int daysAgo)
    {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, -daysAgo);
        return new Date(today.getTimeInMillis());
    }
}
