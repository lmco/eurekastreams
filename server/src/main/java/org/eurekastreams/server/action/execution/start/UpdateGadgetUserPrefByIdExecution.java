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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.start.GadgetUserPrefActionRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * This action updates user prefs for a given gadget. If the gadget does not already have an entry in the db for the
 * user prefs, then a new record will be added to the db.
 * 
 */
public class UpdateGadgetUserPrefByIdExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Local instance of logger.
     */
    private final Log logger = LogFactory.make();

    /**
     * Mapper responsible for updating the existing user pref.
     */
    private final UpdateMapper<Gadget> updateMapper;

    /**
     * Mapper responsible for retrieving the gadget instance that is needed to create a new user pref record if none is
     * already in the db.
     */
    private final FindByIdMapper<Gadget> findGadgetByIdMapper;

    /** Mapper to clear or refresh user's start page data in cache. */
    private final DomainMapper<Long, Object> pageMapper;

    /**
     * Constructor for action.
     * 
     * @param inUpdateMapper
     *            - mapper responsible for gadget user pref updates.
     * @param inFindGadgetByIdMapper
     *            - mapper responsible for retrieving gadget instances.
     * @param inPageMapper
     *            Mapper to clear or refresh user's start page data in cache.
     */
    public UpdateGadgetUserPrefByIdExecution(final UpdateMapper<Gadget> inUpdateMapper,
            final FindByIdMapper<Gadget> inFindGadgetByIdMapper, final DomainMapper<Long, Object> inPageMapper)
    {
        updateMapper = inUpdateMapper;
        findGadgetByIdMapper = inFindGadgetByIdMapper;
        pageMapper = inPageMapper;
    }

    /**
     * Update gadget user preferences by id.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return gadget user preferences.
     */
    @Override
    public String execute(final ActionContext inActionContext)
    {
        GadgetUserPrefActionRequest currentRequest = (GadgetUserPrefActionRequest) inActionContext.getParams();

        // No unescaping/decoding required

        logger.debug("Updating user prefs for gadget: " + currentRequest.getGadgetId() + " with: "
                + currentRequest.getGadgetUserPref());

        Gadget currentGadgetInstance = findGadgetByIdMapper.execute(new FindByIdRequest("Gadget", currentRequest
                .getGadgetId()));

        String oldValue = currentGadgetInstance.getGadgetUserPref();
        String newValue = currentRequest.getGadgetUserPref();

        if (StringUtils.equalsIgnoreCase(oldValue, newValue))
        {
            logger.debug("gadget user prefs didn't change; i refuse to update them.");
            return oldValue;
        }
        logger.debug("gadget user prefs changed; updating them.");

        currentGadgetInstance.setGadgetUserPref(newValue);
        updateMapper.execute(new PersistenceRequest<Gadget>(currentGadgetInstance));

        // clear or refresh user's page data in the cache (since it has old preferences)
        pageMapper.execute(currentGadgetInstance.getOwner().getId());

        return currentGadgetInstance.getGadgetUserPref();

    }

}
