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

import javax.persistence.NoResultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.annotations.RequiresCredentials;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.start.AddGadgetRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.persistence.GadgetDefinitionMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Creates and returns a new Gadget, with the name provided by parameter.
 */
@RequiresCredentials
public class AddGadgetExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(AddGadgetExecution.class);

    /**
     * The DataMapper that lets us build a new gadget.
     */
    private TabMapper tabMapper = null;

    /**
     * The DataMapper that lets us get a person.
     */
    private PersonMapper personMapper = null;

    /**
     * The DataMapper that finds the gadget definition mapper by definition URL.
     */
    private GadgetDefinitionMapper gadgetDefinitionMapper = null;

    /**
     * Domain mapper to delete keys.
     */
    private DomainMapper<Set<String>, Boolean> deleteKeysMapper;

    /**
     * Constructor.
     * 
     * @param inTabMapper
     *            used to load the tab that will get the new gadget.
     * @param inPersonMapper
     *            used to load the person.
     * @param inGadgetDefinitionMapper
     *            used to find the gadget that will be added.
     * @param inDeleteKeysMapper
     *            mapper to delete cache keys.
     */
    public AddGadgetExecution(final TabMapper inTabMapper, final PersonMapper inPersonMapper,
            final GadgetDefinitionMapper inGadgetDefinitionMapper,
            final DomainMapper<Set<String>, Boolean> inDeleteKeysMapper)
    {
        tabMapper = inTabMapper;
        personMapper = inPersonMapper;
        gadgetDefinitionMapper = inGadgetDefinitionMapper;
        deleteKeysMapper = inDeleteKeysMapper;
    }

    /**
     * Shuffle the gadget collection by shifting all the zoneIndexes upward in the first zone in the input
     * gadgetCollection.
     * 
     * @param gadgetCollection
     *            the collection to shuffle.
     */
    private void shiftGadget(final List<Gadget> gadgetCollection)
    {
        for (Gadget gadget : gadgetCollection)
        {
            if (gadget.getZoneNumber() == 0)
            {
                gadget.setZoneIndex(gadget.getZoneIndex() + 1);
            }
        }
    }

    /**
     * Create and return a new Gadget.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return the new gadget
     * @throws ExecutionException
     *             can result from bad arguments, the user not being logged in, or not finding the user in the database
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        AddGadgetRequest request = (AddGadgetRequest) inActionContext.getParams();
        Long tabId = request.getTabId();
        String gadgetDefUrl = request.getGadgetDefinitionUrl();
        Person owner = personMapper.findByAccountId(inActionContext.getPrincipal().getAccountId());
        Tab tab = owner.getTabs(TabGroupType.START).get(0);

        if (null != tabId)
        {
            tab = tabMapper.findById(tabId);
        }

        try
        {
            GadgetDefinition gadgetDef;

            // UUID identified by starting with { and ending with }
            if (gadgetDefUrl.startsWith("{") && gadgetDefUrl.substring(gadgetDefUrl.length() - 1).equals("}"))
            {
                // gadget def is identified by a UUID
                gadgetDef = gadgetDefinitionMapper.findByUUID(gadgetDefUrl.substring(1, gadgetDefUrl.length() - 1));
            }
            else
            {
                // gadget def is identified by a URL.
                gadgetDef = gadgetDefinitionMapper.findByUrl(gadgetDefUrl);
            }

            /*
             * If gadgetDef is not found, throw an exception, something went wrong, most likely a bad UUID or URL.
             */
            if (null == gadgetDef)
            {
                throw new ExecutionException("Unable to instantiate gadgetDef.");
            }

            // increment the indexes of any gadgets in that zone
            shiftGadget(tab.getGadgets());

            // get the owner

            // create the new gadget at the top of the last zone
            Gadget gadget = new Gadget(gadgetDef, 0, 0, owner, request.getUserPrefs() == null ? "" : request
                    .getUserPrefs());

            // insert the new gadget - room has been made for it
            tab.getGadgets().add(gadget);

            // commit our operations
            tabMapper.flush();

            deleteKeysMapper.execute(Collections.singleton(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID + owner.getId()));

            // return it
            return gadget;
        }
        catch (NoResultException ex)
        {
            log.error("Could not add Gadget because tab not found: " + owner.getUniqueId());
            throw new ExecutionException("Could not add Gadget because tab not found: " + owner.getUniqueId());
        }
    }
}
