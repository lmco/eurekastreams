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
package org.eurekastreams.server.action.execution.start;

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.start.ReorderGadgetRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.TabMapper;

/**
 * Move a gadget.
 *
 */
public class ReorderGadgetExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Used to load and save the Tab and the Gadgets on it.
     */
    private TabMapper tabMapper = null;

    /**
     * Constructor.
     *
     * @param mapper
     *            injected mapper
     */
    public ReorderGadgetExecution(final TabMapper mapper)
    {
        tabMapper = mapper;
    }

    /**
     * Move a gadget.
     *
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return {@link Tab}.
     */
    @Override
    public Tab execute(final PrincipalActionContext inActionContext)
    {
        ReorderGadgetRequest request = (ReorderGadgetRequest) inActionContext.getParams();
        Long targetTabId = request.getCurrentTabId();
        Long gadgetId = request.getGadgetId();
        Integer targetZoneNumber = request.getTargetZoneNumber();
        Integer targetZoneIndex = request.getTargetZoneIndex();

        try
        {
            TabTemplate sourceTemplate;
            // Look to the state bag first then retrieve by mapper.
            if (inActionContext.getState().get("sourceTemplate") != null)
            {
                sourceTemplate = (TabTemplate) inActionContext.getState().get("sourceTemplate");
            }
            else
            {
                sourceTemplate = tabMapper.findByGadgetId(gadgetId);
            }

            TabTemplate destinationTemplate;
            // Look to the state bag first then retrieve by mapper.
            if (inActionContext.getState().get("destinationTemplate") != null)
            {
                destinationTemplate = (TabTemplate) inActionContext.getState().get("destinationTemplate");
            }
            else
            {
                Tab destinationTab = tabMapper.findById(targetTabId);
                destinationTemplate = destinationTab.getTemplate();
            }

            List<Gadget> gadgets = sourceTemplate.getGadgets();

            Gadget gadget = findTargetGadget(gadgets, gadgetId);

            int oldZoneNumber = gadget.getZoneNumber();
            int oldZoneIndex = gadget.getZoneIndex();

            if (log.isDebugEnabled())
            {
                log.debug("old tabId, zoneNumber and zoneIndex: " + sourceTemplate.getId() + ", " + oldZoneNumber
                        + ", " + oldZoneIndex);
            }

            tabMapper.moveGadget(gadget.getId(), sourceTemplate.getId(), oldZoneIndex, oldZoneNumber,
                    destinationTemplate.getId(), targetZoneIndex, targetZoneNumber);

            return tabMapper.findById(targetTabId);
        }
        catch (Exception ex)
        {
            throw new ExecutionException("Error occurred moving gadget.", ex);
        }
    }

    /**
     * Utility method to pick the gadget that will be moving from the whole set of gadgets.
     *
     * @param gadgets
     *            the collection of gadgets
     * @param gadgetId
     *            the id of the target gadget
     * @return the Gadget that has the matching id, or null if not found
     */
    private Gadget findTargetGadget(final List<Gadget> gadgets, final Long gadgetId)
    {
        for (Gadget gadget : gadgets)
        {
            if (gadget.getId() == gadgetId)
            {
                return gadget;
            }
        }
        return null;
    }
}
