/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamHashTag;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Test fixture for DeleteOldStreamHashTagRecordsDbMapper.
 */
public class DeleteOldStreamHashTagRecordsDbMapperTest extends MapperTest
{
    /**
     * test execute.
     */
    @Test
    public void testExecute()
    {
        DeleteOldStreamHashTagRecordsDbMapper sut = new DeleteOldStreamHashTagRecordsDbMapper();
        sut.setEntityManager(getEntityManager());

        final Integer hoursInDay = 24;
        Calendar calTwoDaysAgo = Calendar.getInstance();
        calTwoDaysAgo.add(Calendar.HOUR, -hoursInDay * 2);
        Date twoDaysAgo = calTwoDaysAgo.getTime();

        Calendar calOneDayAgo = Calendar.getInstance();
        calOneDayAgo.add(Calendar.HOUR, -hoursInDay);
        Date oneDayAgo = calOneDayAgo.getTime();

        // set all activities to be two days old
        getEntityManager().createQuery("UPDATE StreamHashTag SET activityDate = :activityDate").setParameter(
                "activityDate", twoDaysAgo).executeUpdate();

        // now set two to be 1 day old
        getEntityManager().createQuery("UPDATE StreamHashTag SET activityDate = :activityDate WHERE id IN (15,16)")
                .setParameter("activityDate", oneDayAgo).executeUpdate();

        final Integer dayAndAHalfAgoHours = 36;
        Calendar calDayAndAHalfAgo = Calendar.getInstance();
        calDayAndAHalfAgo.add(Calendar.HOUR, -dayAndAHalfAgoHours);
        Date dayAndAHalAgo = calDayAndAHalfAgo.getTime();

        Long numberOfActivities = (Long) getEntityManager().createQuery("SELECT COUNT(*) FROM StreamHashTag")
                .getSingleResult();

        Long numberOfRecordsDeleted = new Long(sut.execute(dayAndAHalAgo));

        assertEquals(new Long(numberOfActivities - 2L), numberOfRecordsDeleted);

        List<StreamHashTag> streamHashTags = getEntityManager().createQuery("FROM StreamHashTag").getResultList();
        assertEquals(2, streamHashTags.size());

        final Long fifteen = 15L;
        final Long sixteen = 16L;
        boolean found15 = false;
        boolean found16 = false;
        for (StreamHashTag st : streamHashTags)
        {
            if (st.getId() == fifteen)
            {
                found15 = true;
            }
            if (st.getId() == sixteen)
            {
                found16 = true;
            }
        }
        assertTrue(found15);
        assertTrue(found16);
    }
}
