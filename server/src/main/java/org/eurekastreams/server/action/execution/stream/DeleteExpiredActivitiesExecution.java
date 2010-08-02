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
package org.eurekastreams.server.action.execution.stream;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetExpiredActivities;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;

import com.ibm.icu.util.Calendar;

/**
 * This execution strategy performs a cleanup of expired activites from the system based on the system setting for the
 * number of days before content/activity is considered old or expired. Once the expired activities are found, they are
 * batched and then each batch is submitted to the DeleteActivitiesByIds async action which then proceeds with the
 * cleanup by batches.
 *
 */
public class DeleteExpiredActivitiesExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local logger instance.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to get the current system settings.
     */
    private DomainMapper<MapperRequest, SystemSettings> settingsMapper;

    /**
     * Mapper to get a list of all expired activities.
     */
    private GetExpiredActivities expiredActivitiesMapper;

    /**
     * Number of expired activities to process at a time so the queue can handle smaller tasks.
     */
    private int chunkSize;

    /**
     * Constructor.
     *
     * @param inSettingsMapper
     *            the settings mapper.
     * @param inExpiredActivitiesMapper
     *            the expired activities mapper.
     * @param inChunkSize
     *            the number of activities to include in a single processing chunk.
     */
    public DeleteExpiredActivitiesExecution(final DomainMapper<MapperRequest, SystemSettings> inSettingsMapper,
            final GetExpiredActivities inExpiredActivitiesMapper, final int inChunkSize)
    {
        settingsMapper = inSettingsMapper;
        expiredActivitiesMapper = inExpiredActivitiesMapper;
        chunkSize = inChunkSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        SystemSettings settings = settingsMapper.execute(null);
        int expirationDays = settings.getContentExpiration();

        // checks to see if content expiration is turned on or not
        if (expirationDays > 0)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, expirationDays * -1);
            List<Long> expired = expiredActivitiesMapper.execute(calendar.getTime());

            if (log.isTraceEnabled())
            {
                log.trace("Expired activity ids to delete: " + expired);
            }

            while (expired.size() > 0)
            {
                int count = expired.size() > chunkSize ? chunkSize : expired.size();

                ArrayList<Long> chunkActivityIds = new ArrayList<Long>(expired.subList(0, count));

                if (log.isTraceEnabled())
                {
                    log.trace("Pushing UserActionRequest to queue for deleteActivitiesByIds with "
                            + chunkActivityIds.size() + " activities");
                }
                inActionContext.getUserActionRequests().add(
                        new UserActionRequest("deleteActivitiesByIds", null, chunkActivityIds));

                expired.subList(0, count).clear();
            }
        }
        return Boolean.TRUE;
    }
}
