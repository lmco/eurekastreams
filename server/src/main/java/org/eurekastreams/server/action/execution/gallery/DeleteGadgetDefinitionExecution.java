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
package org.eurekastreams.server.action.execution.gallery;

import java.io.Serializable;
import java.util.Collection;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.gallery.CompressGadgetZoneRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Deletes a gadget definition and queues tasks to fix the start page tabs of every user who had the gadget.
 */
public class DeleteGadgetDefinitionExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /** Mapper to delete the gadget definition. */
    private final DomainMapper<Long, Void> deleteGadgetDefinitionMapper;

    /** Mapper to get list of affected tab templates. */
    private final DomainMapper<Long, Collection<CompressGadgetZoneRequest>> tabListMapper;

    /** Name of action to initiate. */
    private final String nextAction;

    /**
     * Constructor.
     *
     * @param inDeleteGadgetDefinitionMapper
     *            Mapper to delete the gadget definition.
     * @param inTabListMapper
     *            Mapper to get list of affected tab templates.
     * @param inNextAction
     *            Name of action to initiate.
     */
    public DeleteGadgetDefinitionExecution(final DomainMapper<Long, Void> inDeleteGadgetDefinitionMapper,
            final DomainMapper<Long, Collection<CompressGadgetZoneRequest>> inTabListMapper, final String inNextAction)
    {
        deleteGadgetDefinitionMapper = inDeleteGadgetDefinitionMapper;
        tabListMapper = inTabListMapper;
        nextAction = inNextAction;
    }

    /**
     * @see DeleteGadgetDefinitionExecution
     * @param inActionContext
     *            The action parameter must be a Long which is the ID of the gadget definition to delete.
     * @return Nothing.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        Long gadgetDefinitionId = (Long) inActionContext.getActionContext().getParams();

        // get and queue list of tabs to update
        Collection<CompressGadgetZoneRequest> tabs = tabListMapper.execute(gadgetDefinitionId);
        for (CompressGadgetZoneRequest tab : tabs)
        {
            inActionContext.getUserActionRequests().add(new UserActionRequest(nextAction, null, tab));
        }

        // delete the gadget
        deleteGadgetDefinitionMapper.execute(gadgetDefinitionId);

        return null;
    }
}
