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

import java.util.Date;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper to delete all StreamHashTags from activities inserted before a certain date.
 */
public class DeleteOldStreamHashTagRecordsDbMapper extends BaseArgDomainMapper<Date, Integer>
{
    /**
     * Delete all StreamHashTags from activities before a certain date.
     *
     * @param inStreamHashTagExpireDate
     *            the date to delete all activity posted before
     * @return number of records affected
     */
    @Override
    public Integer execute(final Date inStreamHashTagExpireDate)
    {
        int numberOfRecordsAffected = getEntityManager().createQuery(
                "DELETE FROM StreamHashTag WHERE activityDate < :activityDate").setParameter("activityDate",
                inStreamHashTagExpireDate).executeUpdate();
        return numberOfRecordsAffected;
    }
}
