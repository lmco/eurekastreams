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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.start.SetGadgetStateRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.persistence.GadgetMapper;

/**
 * Deletes a Tab, with the ID provided by parameter.
 */
public class SetGadgetStateExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local instance of logger.
     */
    private final Log logger = LogFactory.make();

    /**
     * The mapper we'll used to retrieve the tab.
     */
    private final GadgetMapper gadgetMapper;

    /**
     * Constructor.
     *
     * @param inGadgetMapper
     *            mapper used to retrieve the Gadget.
     */
    public SetGadgetStateExecution(final GadgetMapper inGadgetMapper)
    {
        gadgetMapper = inGadgetMapper;
    }

    /**
     * {@inheritDoc}. Retrieve a tab, minimize the specified gadget.
     */
    @Override
    public Gadget execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        SetGadgetStateRequest currentRequest = (SetGadgetStateRequest) inActionContext.getParams();

        if (logger.isDebugEnabled())
        {
            logger.debug("Calling Minimize on Gadget: " + currentRequest.getGadgetId() + ". Setting minimize to: "
                    + currentRequest.isMinimized());
        }

        Gadget gadget = gadgetMapper.findById(currentRequest.getGadgetId());

        if (null == gadget)
        {
            throw new ExecutionException("Failed to set minimized state for gadget. Gadget id "
                    + currentRequest.getGadgetId() + " not found");
        }

        gadget.setMinimized(currentRequest.isMinimized());
        gadget.setMaximized(currentRequest.isMaximized());

        gadgetMapper.flush();

        return gadget;
    }

}
