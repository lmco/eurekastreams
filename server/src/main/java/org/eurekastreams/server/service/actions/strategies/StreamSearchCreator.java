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
package org.eurekastreams.server.service.actions.strategies;

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.AddCachedCompositeStreamSearch;

/**
 * Strategy for creating StreamSearch object.
 *
 */
public class StreamSearchCreator extends StreamSearchPersister implements ResourcePersistenceStrategy<StreamSearch>,
        UpdaterStrategy
{
    /**
     * Add Cached Composite Stream Search Mapper.
     */
    private AddCachedCompositeStreamSearch addCachedCompositeStreamSearch;

    /**
     * Constructor.
     *
     * @param inAddCachedCompositeStreamSearch
     *            Add Cached Composite Stream Search Mapper.
     * @param inStreamViewDAO
     *            StreamView FindById DAO.
     * @param inPersonDAO
     *            Person FindById DAO.
     */
    public StreamSearchCreator(final AddCachedCompositeStreamSearch inAddCachedCompositeStreamSearch,
            final FindByIdMapper<StreamView> inStreamViewDAO, final FindByIdMapper<Person> inPersonDAO)
    {
        super(inStreamViewDAO, inPersonDAO);
        this.addCachedCompositeStreamSearch = inAddCachedCompositeStreamSearch;
    }

    /**
     * Returns StreamView object created from passed in parameters.
     *
     * @param inActionContext
     *            the action context
     * @param inFields
     *            StreamSearch parameters.
     * @return StreamSearch object created from passed in parameters.
     */
    @Override
    public StreamSearch getStreamSearch(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        return new StreamSearch(getName(inFields), getStreamView(inFields), getKeywords(inFields));
    }

    /**
     * Persists the StreamSearch to data store.
     *
     * @param inActionContext
     *            the action context
     * @param inFields
     *            StreamSearch parameters.
     * @param inResource
     *            The StreamSearch to persist.
     */
    @Override
    public void persistStreamSearch(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final StreamSearch inResource)
    {
        long userPersonId = inActionContext.getActionContext().getPrincipal().getId();
        Person p = getPersonDAO().execute(new FindByIdRequest("Person", userPersonId));
        p.getStreamSearches().add(inResource);
        getPersonDAO().flush();
        addCachedCompositeStreamSearch.execute(userPersonId, inResource);
    }

    /**
     * Implementation of UpdaterStrategy to avoid the reflective updater. Doesn't do anything, properties are set in
     * "get" method to avoid casting here.
     *
     * @param instance
     *            Object to update.
     * @param properties
     *            parameters.
     */
    public void setProperties(final Object instance, final Map<String, Serializable> properties)
    {
        // do nothing, taken care of in "get" method..
    }
}
