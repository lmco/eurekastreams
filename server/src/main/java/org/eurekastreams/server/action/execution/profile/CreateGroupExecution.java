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
package org.eurekastreams.server.action.execution.profile;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Action for creating a group. This class wraps the old PersistResourceExecution action and allows us to return non
 * DomainEntity to client.
 */
public class CreateGroupExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Persist resource action.
     */
    private TaskHandlerExecutionStrategy<PrincipalActionContext> persistResourceExecution;

    /**
     * Constructor.
     * 
     * @param inPersistResourceExecution
     *            PersistResourceExecution for creating group.
     */
    public CreateGroupExecution(final TaskHandlerExecutionStrategy<PrincipalActionContext> inPersistResourceExecution)
    {
        persistResourceExecution = inPersistResourceExecution;
    }

    /**
     * Create a group. This is a pass through to old PersistResourceExecution so we can retain functionality, but not
     * return DomainEntities to client.
     * 
     * @param inActionContext
     *            the action context.
     * @return {@link DomainGroupModelView} with only minimal fields set that are used by client. This is not a full
     *         group representation.
     */
    @Override
    public DomainGroupModelView execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        DomainGroup createdGroup = (DomainGroup) persistResourceExecution.execute(inActionContext);

        DomainGroupModelView result = new DomainGroupModelView();
        result.setShortName(createdGroup.getShortName());
        result.setParentOrganizationShortName(createdGroup.getParentOrganizationShortName());
        result.setPending(createdGroup.isPending());

        return result;
    }

}
