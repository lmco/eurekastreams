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

import javax.persistence.NoResultException;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.start.GadgetUserPrefActionRequest;
import org.eurekastreams.server.domain.GadgetUserPrefDTO;
import org.eurekastreams.server.persistence.mappers.opensocial.GetGadgetUserPrefMapper;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.GadgetUserPrefRequest;

/**
 * This execution strategy to retrieve Gadget User Preferences by Gadget id.
 */
public class GetGadgetUserPrefByIdExecutionStrategy implements ExecutionStrategy<ActionContext>
{
    /**
     * Local instance of User Pref mapper.
     */
    private GetGadgetUserPrefMapper mapper;

    /**
     * Default value returned to caller if no preferences are found.
     */
    private static final String DEFAULT_USER_PREFS = "{}";

    /**
     * Constructor for User Pref Action.
     *
     * @param inMapper
     *            - gadget user pref mapper instance.
     */
    public GetGadgetUserPrefByIdExecutionStrategy(final GetGadgetUserPrefMapper inMapper)
    {
        mapper = inMapper;
    }

    /**
     * Perform the action execution for retrieving user preferences by id.
     *
     * @param inActionContext
     *            - the action execution context, including the GadgetUserPrefActionRequest
     *
     * @return Serializable GadgetUserPref DTO object.
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        GadgetUserPrefActionRequest request = (GadgetUserPrefActionRequest) inActionContext.getParams();
        String userPrefs;
        try
        {
            GadgetUserPrefDTO currentPrefs = mapper.execute(new GadgetUserPrefRequest(request.getGadgetId(), request
                    .getGadgetUserPref()));
            userPrefs = currentPrefs.getJsonUserPref();
        }
        catch (NoResultException nex)
        {
            userPrefs = DEFAULT_USER_PREFS;
        }
        return userPrefs;
    }

}
