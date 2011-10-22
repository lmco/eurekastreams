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
package org.eurekastreams.server.action.authorization.stream;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.service.actions.strategies.ActivityInteractionType;
import org.eurekastreams.server.service.utility.authorization.ActivityInteractionAuthorizationStrategy;

/**
 * Authorization Strategy for an Activity - Determines if user has permission to modify (Post|Comment|View on) an
 * activity.
 */
public class ActivityAuthorizationStrategy implements AuthorizationStrategy<PrincipalActionContext>
{
    /** Log. */
    private final Log logger = LogFactory.make();

    /** Does the actual work of determining authorization. */
    private final ActivityInteractionAuthorizationStrategy activityAuthorizer;

    /** The type of action being performed on the activity. */
    private final ActivityInteractionType type;

    /** Strategy for getting activity ID from incoming params. */
    private final Transformer<Serializable, Long> activityIdFromParamsTransformer;

    /** Gets activity. */
    private final DomainMapper<Long, ActivityDTO> getActivityDAO;

    /**
     * Constructor.
     *
     * @param inActivityAuthorizer
     *            Does the actual work of determining authorization.
     * @param inType
     *            The type of action being performed on the activity.
     * @param inActivityIdFromParamsTransformer
     *            Strategy for getting activity ID from incoming params.
     * @param inGetActivityDAO
     *            Gets activity.
     */
    public ActivityAuthorizationStrategy(final ActivityInteractionAuthorizationStrategy inActivityAuthorizer,
            final ActivityInteractionType inType,
            final Transformer<Serializable, Long> inActivityIdFromParamsTransformer,
            final DomainMapper<Long, ActivityDTO> inGetActivityDAO)
    {
        activityAuthorizer = inActivityAuthorizer;
        type = inType;
        activityIdFromParamsTransformer = inActivityIdFromParamsTransformer;
        getActivityDAO = inGetActivityDAO;
    }

    /**
     * Determines if user has permission to modify (Post|Comment|View on) an activity.
     *
     * @param inActionContext
     *            the action context
     */
    public void authorize(final PrincipalActionContext inActionContext)
    {
        ActivityDTO activity = null;
        final Principal principal = inActionContext.getPrincipal();
        try
        {
            long activityId = activityIdFromParamsTransformer.transform(inActionContext.getParams());
            activity = getActivityDAO.execute(activityId);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving the activity dto params.", ex);
            throw new AuthorizationException(
                    "This action could not authorize the request due to failure retrieving parameters.", ex);
        }

        if (!activityAuthorizer.authorize(principal.getId(), activity, type))
        {
            throw new AuthorizationException("Current user does not have permissions to "
                    + type.toString().toLowerCase() + " activity.");
        }
    }
}
