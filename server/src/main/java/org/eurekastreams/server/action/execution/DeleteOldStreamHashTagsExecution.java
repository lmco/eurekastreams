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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.db.DeleteOldStreamHashTagRecordsDbMapper;

/**
 * Execution strategy to delete all stream hashtags older than a configurable time.
 */
public class DeleteOldStreamHashTagsExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to delete the old stream hashtags.
     */
    private DeleteOldStreamHashTagRecordsDbMapper deleteMapper;

    /**
     * Number of minutes to keep activity hash tags.
     */
    private Integer activityHashTagExpirationInMinutes;

    /**
     * Constructor.
     *
     * @param inDeleteMapper
     *            mapper to delete old stream hashtags
     * @param inActivityHashTagExpirationInMinutes
     *            number of minutes to keep activity hash tags
     */
    public DeleteOldStreamHashTagsExecution(final DeleteOldStreamHashTagRecordsDbMapper inDeleteMapper,
            final Integer inActivityHashTagExpirationInMinutes)
    {
        deleteMapper = inDeleteMapper;
        activityHashTagExpirationInMinutes = inActivityHashTagExpirationInMinutes;
    }

    /**
     * Delete all StreamHashTags older than the configurable minutes.
     *
     * @param inActionContext
     *            not used
     * @return true
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        log.info("Deleting StreamHashTag records older than " + activityHashTagExpirationInMinutes + " minutes.");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -activityHashTagExpirationInMinutes);
        int numberOfDeletedRecords = deleteMapper.execute(cal.getTime());
        log.info("Deleted " + numberOfDeletedRecords + " stream hashtag records.");
        return true;
    }
}
