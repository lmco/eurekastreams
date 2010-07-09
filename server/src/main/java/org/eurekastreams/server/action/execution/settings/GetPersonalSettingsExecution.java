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
package org.eurekastreams.server.action.execution.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.response.settings.RetrieveSettingsResponse;

/**
 * Retrieves settings for a user using a list of retrieval strategies.
 */
public class GetPersonalSettingsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** List of retrieval strategies to fetch settings and supporting data. */
    private List<SettingsRetriever> retrievers;

    /**
     * Constructor.
     *
     * @param inRetrievers
     *            List of retrieval strategies.
     */
    public GetPersonalSettingsExecution(final List<SettingsRetriever> inRetrievers)
    {
        retrievers = inRetrievers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        RetrieveSettingsResponse response =
                new RetrieveSettingsResponse(new HashMap<String, Object>(), new HashMap<String, Object>());

        long userid = inActionContext.getPrincipal().getId();
        for (SettingsRetriever retriever : retrievers)
        {
            retriever.retrieve(userid, response);
        }

        return response;
    }
}
