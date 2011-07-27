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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.PersonBlockedSuggestion;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Insert a stream bookmark.
 */
public class InsertStreamBlockedSuggestionExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper used to insert blocked suggestions.
     */
    private final InsertMapper<PersonBlockedSuggestion> insertMapper;

    /**
     * Constructor.
     * 
     * @param inInsertMapper
     *            the insert mapper.
     */
    public InsertStreamBlockedSuggestionExecution(final InsertMapper<PersonBlockedSuggestion> inInsertMapper)
    {
        insertMapper = inInsertMapper;
    }

    /**
     * Execute the action.
     * 
     * @param inActionContext
     *            the action context.
     * @return the parameter.
     * @throws ExecutionException
     *             on execution failure.
     */
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        Long ssIdToInsert = (Long) inActionContext.getParams();

        PersonBlockedSuggestion blockedSuggestion = new PersonBlockedSuggestion(ssIdToInsert, inActionContext
                .getPrincipal().getId());
        insertMapper.execute(new PersistenceRequest<PersonBlockedSuggestion>(blockedSuggestion));

        return null;
    }
}
