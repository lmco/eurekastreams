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
import org.eurekastreams.server.persistence.mappers.FindUserStreamSearchById;
import org.eurekastreams.server.persistence.mappers.requests.FindUserStreamFilterByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.UpdateCachedCompositeStreamSearch;

/**
 * Strategy for updating StreamSearch objects.
 *
 */
public class StreamSearchUpdater extends StreamSearchPersister implements UpdaterStrategy
{
    /**
     * The update cached composite stream search mapper.
     */
    private UpdateCachedCompositeStreamSearch updateCachedCompositeStreamSearch;

    /**
     * Find {@link StreamSearch} by user and id DAO.
     */
    private FindUserStreamSearchById streamSearchByUserAndIdDAO;

    /**
     * Constructor.
     *
     * @param inUpdateCachedCompositeStreamSearch
     *            Update Cached Composite Stream Search mapper.
     * @param inStreamViewDAO
     *            StreamView find by id DAO.
     * @param inPersonDAO
     *            Person find by id DAO.
     * @param inStreamSearchByUserAndIdDAO
     *            Find {@link StreamSearch} by user and id DAO.
     */
    public StreamSearchUpdater(final UpdateCachedCompositeStreamSearch inUpdateCachedCompositeStreamSearch,
            final FindByIdMapper<StreamView> inStreamViewDAO, final FindByIdMapper<Person> inPersonDAO,
            final FindUserStreamSearchById inStreamSearchByUserAndIdDAO)
    {
        super(inStreamViewDAO, inPersonDAO);
        streamSearchByUserAndIdDAO = inStreamSearchByUserAndIdDAO;
        this.updateCachedCompositeStreamSearch = inUpdateCachedCompositeStreamSearch;
    }

    /**
     * Gets a StreamSearch based on parameters passed in.
     *
     * @param inActionContext
     *            the action context
     * @param inFields
     *            the resources fields.
     * @return the StreamSearch.
     */
    @Override
    public StreamSearch getStreamSearch(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        return streamSearchByUserAndIdDAO.execute(new FindUserStreamFilterByIdRequest(inActionContext
                .getActionContext().getPrincipal().getId(), ((Long) inFields.get("id")).longValue()));
    }

    /**
     * Persists a resource.
     *
     * @param inActionContext
     *            action context
     * @param inFields
     *            form data from the user describing the resource
     * @param inResource
     *            the resource.
     */
    @Override
    public void persistStreamSearch(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final StreamSearch inResource)
    {
        streamSearchByUserAndIdDAO.flush();
        updateCachedCompositeStreamSearch.execute(inResource);
    }

    /**
     * The method that will set properties on an instance.
     *
     * @param instance
     *            target on which to set values.
     * @param properties
     *            the key/value map for which properties to set to what.
     */
    public void setProperties(final Object instance, final Map<String, Serializable> properties)
    {
        StreamSearch search = (StreamSearch) instance;

        if (properties.containsKey("name"))
        {
            search.setName(((String) properties.get("name")).trim());
        }

        if (properties.containsKey("streamViewId")
                && search.getStreamView().getId() != (Long) properties.get("streamViewId"))
        {
            search.setStreamView(getStreamView(properties));
        }

        if (properties.containsKey("keywords"))
        {
            search.setKeywords(getKeywords(properties));
        }
    }
}
