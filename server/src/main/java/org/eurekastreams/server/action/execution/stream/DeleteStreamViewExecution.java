/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.DeleteCachedCompositeStreamById;
import org.eurekastreams.server.persistence.mappers.stream.DeleteStreamViewAndRelatedSearches;

/**
 * Action to delete a User's StreamView.
 *
 */
public class DeleteStreamViewExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Find by id DAO for person.
     */
    private FindByIdMapper<Person> findPersonById;

    /**
     * The mapper to handle deletion of the composite stream.
     */
    private DeleteCachedCompositeStreamById deleteCachedCompositeStreamById;
    
    /**
     * The mapper to handle deletion of the streamview and related searches.
     */
    private DeleteStreamViewAndRelatedSearches deleteStreamViewAndSearches;

    /**
     * Constructor.
     *
     * @param inDeleteCachedCompositeStreamById
     *            {@link DeleteCachedCompositeStreamById}.
     * @param inFindPersonDAO
     *            {@link FindByIdMapper} for person.
     * @param inDeleteStreamViewAndSearches
     *            {@link DeleteStreamViewAndSearches}.
     */
    public DeleteStreamViewExecution(final DeleteCachedCompositeStreamById inDeleteCachedCompositeStreamById,
            final FindByIdMapper<Person> inFindPersonDAO,
            final DeleteStreamViewAndRelatedSearches inDeleteStreamViewAndSearches)
    {
        deleteCachedCompositeStreamById = inDeleteCachedCompositeStreamById;
        findPersonById = inFindPersonDAO;
        deleteStreamViewAndSearches = inDeleteStreamViewAndSearches;
    }

    /**
     * Deletes a user's StreamView.
     *
     * @param actionContext
     *            action context with principal and param
     * @return True if success, false otherwise.
     */
    @Override
    public Boolean execute(final PrincipalActionContext actionContext)
    {
        // TODO: Change the lookup logic from the executing user to the owner of the composite stream.
        // The authorization must be implemented first to insure the executing user has permissions.
        Person p = findPersonById.execute(new FindByIdRequest("Person", actionContext.getPrincipal().getId()));
        long streamViewId = (Long) actionContext.getParams();

        for (int i = 0; i < p.getStreamViewDefinitions().size(); i++)
        {
            if (p.getStreamViewDefinitions().get(i).getId() == streamViewId)
            {
                // if streamView has a type set, it's read only.
                if (p.getStreamViewDefinitions().get(i).getType() != null)
                {
                    throw new AuthorizationException("Insuffient permissions to delete read-only object.");
                }
                p.getStreamViewDefinitions().remove(i);
                findPersonById.flush();

                deleteCachedCompositeStreamById.execute(actionContext.getPrincipal().getId(), streamViewId);
                
                deleteStreamViewAndSearches.execute(streamViewId);

                return new Boolean(true);
            }
        }

        return new Boolean(false);
    }
}
