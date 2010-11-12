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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.exceptions.GadgetDeletionException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Delete a gadget from a tab and rearrange the remaining gadgets.
 */
public class DeleteGadgetExecution implements ExecutionStrategy<PrincipalActionContext>
{

    /**
     * The value used if findTargetIndex() is unable to find the target gadget.
     */
    private static final int INVALID_INDEX = -1;

    /**
     * The mapper we'll used to retrieve and delete the gadget.
     */
    private final TabMapper tabMapper;

    /**
     * Domain mapper to delete keys.
     */
    private DomainMapper<Set<String>, Boolean> deleteKeysMapper;

    /**
     * Default Constructor.
     * 
     * @param inTabMapper
     *            - instance of the {@link TabMapper} for this action exection.
     * @param inDeleteKeysMapper
     *            mapper to delete cache keys.
     */
    public DeleteGadgetExecution(final TabMapper inTabMapper,
            final DomainMapper<Set<String>, Boolean> inDeleteKeysMapper)
    {
        tabMapper = inTabMapper;
        deleteKeysMapper = inDeleteKeysMapper;
    }

    /**
     * {@inheritDoc}.
     * 
     * Delete a gadget from a tab and rearrange remaining gadgets.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        Long gadgetId = (Long) inActionContext.getParams();

        Tab tab = tabMapper.findTabByGadgetId(gadgetId);

        List<Gadget> gadgets = tab.getGadgets();

        int targetIndex = findTargetIndex(gadgets, gadgetId);

        if (targetIndex == INVALID_INDEX)
        {
            throw new ExecutionException("Gadget could not be deleted because gadget Id " //
                    + gadgetId + " was not found");
        }

        try
        {
            tabMapper.deleteGadget(gadgets.get(targetIndex));
        }
        catch (GadgetDeletionException e)
        {
            throw new ExecutionException("Error occurred deleting gadget.", e);
        }

        tabMapper.flush();

        deleteKeysMapper.execute(Collections.singleton(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID
                + inActionContext.getPrincipal().getId()));

        return tab;
    }

    /**
     * Return the member of the gadgets collection that has the specified id.
     * 
     * @param gadgets
     *            collection of gadgets to search
     * @param gadgetId
     *            id to search for
     * @return index into gadgets for the target Gadget; -1 if not found.
     */
    private int findTargetIndex(final List<Gadget> gadgets, final Long gadgetId)
    {
        for (int i = 0; i < gadgets.size(); i++)
        {
            if (gadgets.get(i).getId() == gadgetId)
            {
                return i;
            }
        }

        return INVALID_INDEX;
    }

}
