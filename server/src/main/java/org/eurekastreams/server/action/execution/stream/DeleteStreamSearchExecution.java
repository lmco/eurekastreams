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

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.DeleteCachedCompositeStreamSearchById;

/**
 * Action to delete a user's StreamSearch.
 */
public class DeleteStreamSearchExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Find by id DAO for person.
     */
    private FindByIdMapper<Person> findPersonById;

    /**
     * The mapper to delete the cached stream search by id.
     */
    private DeleteCachedCompositeStreamSearchById deleteCachedStreamSearchById;

    /**
     * Constructor.
     *
     * @param inDeleteCachedStreamSearchById
     *            {@link DeleteCachedCompositeStreamSearchById}.
     * @param inFindPersonDAO
     *            {@link FindByIdMapper} for person.
     */
    public DeleteStreamSearchExecution(final DeleteCachedCompositeStreamSearchById inDeleteCachedStreamSearchById,
            final FindByIdMapper<Person> inFindPersonDAO)
    {
        this.deleteCachedStreamSearchById = inDeleteCachedStreamSearchById;
        findPersonById = inFindPersonDAO;
    }

    /**
     * Delete's a user's stream search.
     *
     * @param inActionContext
     *            the action context
     * @return whether the stream search was deleted
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        // todo: use an authorizer to do the check?
        Person p = findPersonById.execute(new FindByIdRequest("Person", inActionContext.getPrincipal().getId()));
        long streamSearchId = (Long) inActionContext.getParams();

        int streamSearchSize = p.getStreamSearches().size();
        for (int i = 0; i < streamSearchSize; i++)
        {
            if (p.getStreamSearches().get(i).getId() == streamSearchId)
            {
                p.getStreamSearches().remove(i);
                findPersonById.flush();

                deleteCachedStreamSearchById.execute(p.getId(), streamSearchId);

                return new Boolean(true);
            }
        }

        return new Boolean(false);
    }

}
