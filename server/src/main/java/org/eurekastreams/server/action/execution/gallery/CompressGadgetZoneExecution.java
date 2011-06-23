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
import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.action.request.gallery.CompressGadgetZoneRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Removes gaps in the gadget order/sequence from a gadget zone on a tab template.
 */
public class CompressGadgetZoneExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /** Mapper to retrieve the gadgets for the zone. */
    private final DomainMapper<CompressGadgetZoneRequest, List<Gadget>> gadgetMapper;

    /** Mapper to refresh user's start page data in cache. */
    private final DomainMapper<Long, Object> pageMapper;

    /**
     * Constructor.
     *
     * @param inGadgetMapper
     *            Mapper to retrieve the gadgets for the zone.
     * @param inPageMapper
     *            Mapper to refresh user's start page data in cache.
     */
    public CompressGadgetZoneExecution(final DomainMapper<CompressGadgetZoneRequest, List<Gadget>> inGadgetMapper,
            final DomainMapper<Long, Object> inPageMapper)
    {
        gadgetMapper = inGadgetMapper;
        pageMapper = inPageMapper;
    }

    /**
     * @see CompressGadgetZoneExecution
     * @param inActionContext
     *            The parameter to the action must be a CompressGadgetZoneRequest containing the tab template id and
     *            zone number of the zone to compress. The request should also contain the person ID of the tab owner;
     *            if it does, that user's start page data will be reloaded into cache. (A null owner can be valid; a
     *            gadget on one of the templates used for creating user pages may have been deleted.)
     * @return Nothing.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        CompressGadgetZoneRequest request = (CompressGadgetZoneRequest) inActionContext.getActionContext().getParams();

        // get the gadgets and update the order
        List<Gadget> gadgets = gadgetMapper.execute(request);
        int count = gadgets.size();
        for (int i = 0; i < count; i++)
        {
            gadgets.get(i).setZoneIndex(i);
        }

        // reload cache
        if (request.getTabTemplateOwnerId() != null)
        {
            pageMapper.execute(request.getTabTemplateOwnerId());
        }

        return null;
    }
}
