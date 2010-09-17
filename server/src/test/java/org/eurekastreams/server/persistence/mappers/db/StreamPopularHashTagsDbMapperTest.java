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

import java.util.Calendar;
import java.util.List;

import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.stream.StreamPopularHashTagsReportDTO;
import org.junit.Test;

/**
 * Test fixture for StreamPopularHashTagsDbMapper.
 */
public class StreamPopularHashTagsDbMapperTest extends MapperTest
{
    /**
     * Test execute when asking for less popular hashtags than exist.
     */
    @Test
    public void testExecuteWithLimitOnCount()
    {
        getEntityManager().createQuery("UPDATE StreamHashTag SET activityDate=:activityDate").setParameter(
                "activityDate", Calendar.getInstance().getTime()).executeUpdate();

        StreamPopularHashTagsDbMapper sut = new StreamPopularHashTagsDbMapper(9, 3);
        sut.setEntityManager(getEntityManager());

        StreamPopularHashTagsReportDTO tagsReport = sut.execute(new StreamPopularHashTagsRequest(ScopeType.ORGANIZATION,
                "tstorgname"));

        List<String> tags = tagsReport.getPopularHashTags();
        assertEquals(3, tags.size());

        // tstorgname has tags #bar(4), #foo(3), #development(2), #java(1)
        assertEquals("#bar", tags.get(0));
        assertEquals("#foo", tags.get(1));
        assertEquals("#development", tags.get(2));
    }

    /**
     * Test execute when asking for more popular hashtags than exist.
     */
    @Test
    public void testExecuteWithNoLimitOnCount()
    {
        getEntityManager().createQuery("UPDATE StreamHashTag SET activityDate=:activityDate").setParameter(
                "activityDate", Calendar.getInstance().getTime()).executeUpdate();

        StreamPopularHashTagsDbMapper sut = new StreamPopularHashTagsDbMapper(9, 8);
        sut.setEntityManager(getEntityManager());

        StreamPopularHashTagsReportDTO tagsReport = sut.execute(new StreamPopularHashTagsRequest(ScopeType.ORGANIZATION,
                "tstorgname"));

        List<String> tags = tagsReport.getPopularHashTags();
        assertEquals(4, tags.size());

        // tstorgname has tags #bar(4), #foo(3), #development(2), #java(1)
        assertEquals("#bar", tags.get(0));
        assertEquals("#foo", tags.get(1));
        assertEquals("#development", tags.get(2));
        assertEquals("#java", tags.get(3));
    }

    /**
     * Test execute when asking for more than are allowed.
     */
    @Test
    public void testExecuteWithSomeOldActivity()
    {
        getEntityManager().createQuery("UPDATE StreamHashTag SET activityDate = :activityDate").setParameter(
                "activityDate", Calendar.getInstance().getTime()).executeUpdate();

        Calendar oldActivity = Calendar.getInstance();
        oldActivity.add(Calendar.YEAR, 0 - 9);
        getEntityManager().createQuery(
                "UPDATE StreamHashTag SET activityDate = :activityDate "
                        + "WHERE hashTagId in (SELECT id FROM HashTag WHERE content = :oldTag)").setParameter(
                "activityDate", oldActivity.getTime()).setParameter("oldTag", "#bar").executeUpdate();

        StreamPopularHashTagsDbMapper sut = new StreamPopularHashTagsDbMapper(9, 9);
        sut.setEntityManager(getEntityManager());

        StreamPopularHashTagsReportDTO tagsReport = sut.execute(new StreamPopularHashTagsRequest(ScopeType.ORGANIZATION,
                "tstorgname"));

        List<String> tags = tagsReport.getPopularHashTags();
        assertEquals(3, tags.size());

        // tstorgname has tags #bar(4), #foo(3), #development(2), #java(1), but #bar was just expired
        assertEquals("#foo", tags.get(0));
        assertEquals("#development", tags.get(1));
        assertEquals("#java", tags.get(2));
    }

    /**
     * Test execute for a person.
     */
    @Test
    public void testExecuteForPerson()
    {
        getEntityManager().createQuery("UPDATE StreamHashTag SET activityDate=:activityDate").setParameter(
                "activityDate", Calendar.getInstance().getTime()).executeUpdate();

        StreamPopularHashTagsDbMapper sut = new StreamPopularHashTagsDbMapper(9, 9);
        sut.setEntityManager(getEntityManager());

        StreamPopularHashTagsReportDTO tagsReport = sut.execute(new StreamPopularHashTagsRequest(ScopeType.PERSON,
                "smithers"));
        List<String> tags = tagsReport.getPopularHashTags();
        assertEquals(2, tags.size());

        // Smithers has tags #soda(5), one of #foo(1)
        assertEquals("#soda", tags.get(0));
        assertEquals("#foo", tags.get(1));
    }
}
