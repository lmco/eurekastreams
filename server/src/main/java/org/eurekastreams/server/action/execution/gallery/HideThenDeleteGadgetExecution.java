/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.persistence.GalleryItemMapper;

/**
 * Action which hides a gadget then asynchronously deletes it.
 */
public class HideThenDeleteGadgetExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /** Mapper to update the gadget definition. */
    private final GalleryItemMapper<GadgetDefinition> galleryItemMapper;

    /** Name of action to initiate. */
    private final String nextAction;

    /**
     * Constructor.
     *
     * @param inGalleryItemMapper
     *            Mapper to update the gadget definition.
     * @param inNextAction
     *            Name of action to initiate.
     */
    public HideThenDeleteGadgetExecution(final GalleryItemMapper<GadgetDefinition> inGalleryItemMapper,
            final String inNextAction)
    {
        galleryItemMapper = inGalleryItemMapper;
        nextAction = inNextAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        Long gadgetDefinitionId = (Long) inActionContext.getActionContext().getParams();

        // hide definition until we can delete it (asynchronously)
        GadgetDefinition gadgetDefinition = galleryItemMapper.findById(gadgetDefinitionId);
        gadgetDefinition.setShowInGallery(false);

        inActionContext.getUserActionRequests().add(new UserActionRequest(nextAction, null, gadgetDefinitionId));

        return null;
    }

}
