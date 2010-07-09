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

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.exceptions.GadgetUndeletionException;

/**
 * Restores a previously deleted gadget to a tab.
 */
public class UndeleteGadgetExecution implements ExecutionStrategy<PrincipalActionContext>
{

    /**
     * The mapper used to get the Tab that owns the Gadget.
     */
    private final TabMapper tabMapper;

    /**
     * Constructor.
     *
     * @param mapper
     *            to get the Tab that owns the gadget.
     */
    public UndeleteGadgetExecution(final TabMapper mapper)
    {
        tabMapper = mapper;
    }

    /**
     * {@inheritDoc}. Restore the deleted Gadget and return the Tab with all Gadgets in their correct positions.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        Long gadgetId = (Long) inActionContext.getParams();

        try
        {
            return tabMapper.undeleteGadget(gadgetId);
        }
        catch (GadgetUndeletionException gue)
        {
            throw new ExecutionException("Error occurred undeleting gadget.", gue);
        }
    }

}
