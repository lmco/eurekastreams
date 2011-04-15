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

import java.util.Date;

import com.ibm.icu.util.Calendar;

/**
 * Helper strategy to determine if a date is a weekday or weekend.
 */
public class DayOfWeekStrategy
{
    /**
     * Determine whether the input date is a weekday.
     * 
     * @param inDate
     *            the date to check
     * @return whether the input date is a weekday
     */
    public boolean isWeekday(final Date inDate)
    {
        Calendar date = Calendar.getInstance();
        date.setTime(inDate);
        return date.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY;
    }
}
