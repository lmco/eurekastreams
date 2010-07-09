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
package org.eurekastreams.server.action.execution.settings;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.RefreshMembership;

/**
 * Wrapper around RefreshMembershipMapper so the call can be scheduled and executed asynchronously.
 */
public class RefreshMembershipExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private Log logger = LogFactory.make();

    /**
     * Mapper to do the real work.
     */
    private RefreshMembership membershipMapper;

    /**
     * Constructor.
     * 
     * @param inMembershipMapper
     *            the mapper to set.
     */
    public RefreshMembershipExecution(final RefreshMembership inMembershipMapper)
    {
        membershipMapper = inMembershipMapper;
    }

    /**
     * Makes a call to the mapper to do the actual work.
     * 
     * @param inActionContext
     *            {@link ActionConext}
     * @return Null.
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        Date start = new Date();
        try
        {
            membershipMapper.execute();
        }
        catch (Exception ex)
        {
            logger.error("Error occurred refreshing system membership", ex);
        }
        Date end = new Date();

        logger.info("Membership Refresh Job: elapsed time: "
                + DurationFormatUtils.formatDurationHMS(end.getTime() - start.getTime()));
        return null;
    }

}
