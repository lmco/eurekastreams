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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.authorization.CoordinatorAccessAuthorizer;
import org.eurekastreams.server.action.request.transformer.RequestTransformer;

/**
 * Execution to determine if current user has coordinator permissions on entity.
 * 
 */
public class CoordinatorAccessAuthorizerExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Transform entity id from request.
     */
    private RequestTransformer entityIdTransformer;

    /**
     * Permission checker.
     */
    private CoordinatorAccessAuthorizer<Long, Long> entityPermissionsChecker;

    /**
     * Constructor.
     * 
     * @param inEntityIdtransformer
     *            Transform entity id from request.
     * @param inEntityPermissionsChecker
     *            Permission checker.
     */
    public CoordinatorAccessAuthorizerExecution(final RequestTransformer inEntityIdtransformer,
            final CoordinatorAccessAuthorizer<Long, Long> inEntityPermissionsChecker)
    {
        entityIdTransformer = inEntityIdtransformer;
        entityPermissionsChecker = inEntityPermissionsChecker;
    }

    /**
     * Determine if current user has coordinator permissions recursively.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return true if user has coordinator access recursively, false otherwise.
     */
    @Override
    public Boolean execute(final PrincipalActionContext inActionContext)
    {
        Long entityId = Long.valueOf((String) entityIdTransformer.transform(inActionContext));
        Long personId = inActionContext.getPrincipal().getId();

        boolean hasAccess = entityPermissionsChecker.hasCoordinatorAccessRecursively(personId, entityId);

        log.info("PersonId: " + personId + " " + (hasAccess ? "has" : "does not have")
                + " access to entity with entityId: " + entityId);

        return hasAccess;
    }

}
