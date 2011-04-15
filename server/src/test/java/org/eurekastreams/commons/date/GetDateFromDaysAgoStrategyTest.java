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

import junit.framework.Assert;

import org.junit.Test;

import com.ibm.icu.util.Calendar;

/**
 * Test fixture for GetDateFromDaysAgoStrategy.
 */
public class GetDateFromDaysAgoStrategyTest
{
    /**
     * Number of ms in a day.
     */
    private final long msInDay = 86400000;

    /**
     * Number of ms in 1 minute.
     */
    private final long msIn1Min = 60000;

    /**
     * System under test.
     */
    private GetDateFromDaysAgoStrategy sut = new GetDateFromDaysAgoStrategy();

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        Calendar day = Calendar.getInstance();
        Date yesterday = sut.execute(1);

        // get the milliseconds difference
        long diff = day.getTimeInMillis() - yesterday.getTime() - msInDay;

        // assert the difference is 24 hours (within a minute, in case this test is ridiculously slow :))
        Assert.assertTrue(diff < msIn1Min);
    }
}
