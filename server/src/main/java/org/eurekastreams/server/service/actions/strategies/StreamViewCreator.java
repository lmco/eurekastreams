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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.GetStreamScopeById;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.AddCachedCompositeStream;

/**
 * Strategy for creating StreamView for a user.
 */
public class StreamViewCreator implements ResourcePersistenceStrategy<StreamView>, UpdaterStrategy
{
    /**
     * Add Cached Composite Stream View Mapper.
     */
    private AddCachedCompositeStream addCachedCompositeStream;

    /**
     * FindById mapper.
     */
    private FindByIdMapper<Person> findPersonById;

    /**
     * CompositeStreamCreator.
     */
    private GetStreamScopeById getStreamScopeById;

    /**
     * Constructor.
     *
     * @param inAddCachedCompositeStream
     *            The cached composite stream mapper.
     * @param inMapper
     *            Person FindById mapper.
     * @param inGetStreamScopeById
     *            GetStreamScopeById mapper.
     */
    public StreamViewCreator(final AddCachedCompositeStream inAddCachedCompositeStream,
            final FindByIdMapper<Person> inMapper, final GetStreamScopeById inGetStreamScopeById)
    {
        addCachedCompositeStream = inAddCachedCompositeStream;
        findPersonById = inMapper;
        getStreamScopeById = inGetStreamScopeById;
    }

    /**
     * Returns StreamView object created from passed in parameters.
     *
     * @param inActionContext
     *            Current user information.
     * @param inFields
     *            StreamView parameters.
     * @return StreamView object created from passed in parameters.
     */
    public StreamView get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        StreamView result = new StreamView();
        return result;
    }

    /**
     * Persists the StreamView to data store.
     *
     * @param inActionContext
     *            action context
     * @param inFields
     *            fields
     * @param inResource
     *            The StreamView to persist.
     */
    public void persist(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final StreamView inResource)
    {
        Person p = findPersonById.execute(new FindByIdRequest("Person", inActionContext.getActionContext()
                .getPrincipal().getId()));

        p.getStreamViewDefinitions().add(inResource);
        findPersonById.flush();

        addCachedCompositeStream.execute(inActionContext.getActionContext().getPrincipal().getId(), inResource);
    }

    /**
     * Implementation of UpdaterStrategy to avoid the reflective updater.
     *
     * @param instance
     *            Object to update.
     * @param properties
     *            parameters.
     */
    @SuppressWarnings("unchecked")
    public void setProperties(final Object instance, final Map<String, Serializable> properties)
    {
        StreamView streamView = (StreamView) instance;
        streamView.setName((String) properties.get("name"));
        Set scopes = new HashSet<StreamScope>();
        scopes.addAll((List<StreamScope>) properties.get("scopes"));
        streamView.setIncludedScopes(getStreamScopeById.execute(scopes));
    }
}
