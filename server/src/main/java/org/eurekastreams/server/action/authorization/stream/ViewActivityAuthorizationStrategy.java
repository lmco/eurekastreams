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
package org.eurekastreams.server.action.authorization.stream;

import java.util.Arrays;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.execution.stream.ActivitySecurityTrimmer;

/**
 * Authorize if current user has permissions to view given activity id. This ONLY restricts viewing activities posted to
 * private groups, activities posted to public group or any personal stream are visible to anyone.
 * 
 */
public class ViewActivityAuthorizationStrategy implements AuthorizationStrategy<ServiceActionContext>
{
    /**
     * Trims out unauthorized activity.
     */
    private ActivitySecurityTrimmer securityTrimmer;

    /**
     * Constructor.
     * 
     * @param inSecurityTrimmer
     *            Trims unauthorized activity..
     */
    public ViewActivityAuthorizationStrategy(final ActivitySecurityTrimmer inSecurityTrimmer)
    {
        securityTrimmer = inSecurityTrimmer;
    }

    /**
     * Authorize if current user has permissions to view given activity id. Currently only restriction on viewing
     * activities is on activities posted to private groups, activities post to public group or any personal stream are
     * visible to anyone.
     * 
     * @param inActionContext
     *            ActionContext for request.
     */
    public void authorize(final ServiceActionContext inActionContext)
    {
        // Unauthorized if all results are trimed away.
        if (securityTrimmer.trim(Arrays.asList((Long) inActionContext.getParams()),
                inActionContext.getPrincipal().getId()).size() == 0)
        {
            throw new AuthorizationException("Current user does not have access right to view activity.");
        }
    }

}
