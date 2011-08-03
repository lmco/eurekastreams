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
package org.eurekastreams.server.service.actions.strategies;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.commons.date.GetDateFromDaysAgoStrategy;
import org.eurekastreams.commons.date.WeekdaysInDateRangeStrategy;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.TempWeekdaysSinceDate;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.db.DeleteAllTempWeekdaysSinceDateDbMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

import com.ibm.icu.util.Calendar;

/**
 * Strategy to populate the TempWeekdaysSinceDate table with the last N days.
 */
public class RepopulateTempWeekdaysSinceDateStrategy
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Constructor.
     * 
     * @param inWeekdaysInDateRangeStrategy
     *            Strategy to populate the number of weekdays since different dates in the database for Discover page
     *            list generation.
     * @param inDaysAgoDateStrategy
     *            strategy to get a date from N days ago.
     * @param inDeleteAllTempWeekdaysSinceDateDbMapper
     *            Mapper to delete all data in TempWeekdaysSinceDateDbMapper.
     * @param inInsertMapper
     *            Mapper to insert TempWeekdaysSinceDate.
     */
    public RepopulateTempWeekdaysSinceDateStrategy(final WeekdaysInDateRangeStrategy inWeekdaysInDateRangeStrategy,
            final GetDateFromDaysAgoStrategy inDaysAgoDateStrategy,
            final DeleteAllTempWeekdaysSinceDateDbMapper inDeleteAllTempWeekdaysSinceDateDbMapper,
            final InsertMapper<TempWeekdaysSinceDate> inInsertMapper)
    {
        super();
        weekdaysInDateRangeStrategy = inWeekdaysInDateRangeStrategy;
        daysAgoDateStrategy = inDaysAgoDateStrategy;
        deleteAllTempWeekdaysSinceDateDbMapper = inDeleteAllTempWeekdaysSinceDateDbMapper;
        insertMapper = inInsertMapper;
    }

    /**
     * Strategy to populate the number of weekdays since different dates in the database for Discover page list
     * generation.
     */
    private WeekdaysInDateRangeStrategy weekdaysInDateRangeStrategy;

    /**
     * strategy to get a date from N days ago.
     */
    private final GetDateFromDaysAgoStrategy daysAgoDateStrategy;

    /**
     * Mapper to delete all data in TempWeekdaysSinceDateDbMapper.
     */
    private DeleteAllTempWeekdaysSinceDateDbMapper deleteAllTempWeekdaysSinceDateDbMapper;

    /**
     * Mapper to insert TempWeekdaysSinceDate.
     */
    private InsertMapper<TempWeekdaysSinceDate> insertMapper;

    /**
     * Update the TempWeekdaysSinceDate table with the data for the last N days.
     * 
     * @param inDaysToCalculate
     *            the number of days to calculate.
     */
    public void execute(final long inDaysToCalculate)
    {
        // delete all existing data
        deleteAllTempWeekdaysSinceDateDbMapper.execute();

        Calendar day = Calendar.getInstance();
        day.add(Calendar.DATE, -1);
        Date today = DateDayExtractor.getStartOfDay(new Date());

        // loop through N days, populating the data
        for (int i = 1; i <= inDaysToCalculate; i++)
        {
            Date date = DateDayExtractor.getStartOfDay(daysAgoDateStrategy.execute(i));
            int weekdaysCount = weekdaysInDateRangeStrategy.getWeekdayCountBetweenDates(date, today);
            TempWeekdaysSinceDate record = new TempWeekdaysSinceDate(date.getTime(), weekdaysCount);

            log.info("Calculated " + weekdaysCount + " weekdays between now and " + date);
            insertMapper.execute(new PersistenceRequest<TempWeekdaysSinceDate>(record));
        }
    }
}
