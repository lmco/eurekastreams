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
package org.eurekastreams.server.action.request.transformer;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;

/**
 * Request transformer that converts an activity id to its recipient organization id.
 */
public class ActivityIdToRecipientParentOrgIdRequestTransformer implements RequestTransformer
{
    /**
     * Log.
     */
    private static Log log = LogFactory.make();

    /**
     * Mapper to get activities.
     */
    private BulkActivitiesMapper activityMapper;

    /**
     * Constructor.
     *
     * @param inActivityMapper
     *            the mapper to get activities.
     */
    public ActivityIdToRecipientParentOrgIdRequestTransformer(final BulkActivitiesMapper inActivityMapper)
    {
        activityMapper = inActivityMapper;
    }

    /**
     * Extracts the org id from the request.
     *
     * @param inActionContext
     *            action context.
     * @return The org id.
     */
    @Override
    public Serializable transform(final ActionContext inActionContext)
    {
        Long activityId = (Long) inActionContext.getParams();
        Long recipientParentOrgId = activityMapper.execute(activityId, null).getRecipientParentOrgId();

        log.info("Found recipient org id for activity #" + activityId + " = " + recipientParentOrgId);
        return recipientParentOrgId.toString();
    }
}
