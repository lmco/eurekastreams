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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Abstract parent for StreamSearch create and update strategies.
 */
public abstract class StreamSearchPersister implements ResourcePersistenceStrategy<StreamSearch>
{
    /**
     * StreamView dao.
     */
    private FindByIdMapper<StreamView> streamViewDAO;

    /**
     * Person dao.
     */
    private FindByIdMapper<Person> personDAO;

    /**
     * Constructor.
     *
     * @param inStreamViewDAO
     *            StreamView FindById DAO.
     * @param inPersonDAO
     *            Person FindById DAO.
     */
    public StreamSearchPersister(final FindByIdMapper<StreamView> inStreamViewDAO,
            final FindByIdMapper<Person> inPersonDAO)
    {
        streamViewDAO = inStreamViewDAO;
        personDAO = inPersonDAO;
    }

    /**
     * Validate params and call concrete version of "get".
     *
     * @param inActionContext
     *            the action context
     * @param inFields
     *            StreamSearch parameters.
     * @return StreamSearch object from passed in parameters.
     */
    public StreamSearch get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        return getStreamSearch(inActionContext, inFields);
    }

    /**
     * Returns StreamView object from passed in parameters.
     *
     * @param inActionContext
     *            action context
     * @param inFields
     *            StreamSearch parameters.
     * @return StreamSearch object from passed in parameters.
     */
    public abstract StreamSearch getStreamSearch(TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            Map<String, Serializable> inFields);

    /**
     * Persists the StreamSearch to data store.
     *
     * @param inActionContext
     *            action context
     * @param inFields
     *            StreamSearch parameters.
     * @param inResource
     *            The StreamSearch to persist.
     */
    public abstract void persistStreamSearch(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final StreamSearch inResource);

    /**
     * Persists the StreamSearch to data store.
     *
     * @param inActionContext
     *            action context
     * @param inFields
     *            StreamSearch parameters.
     * @param inResource
     *            The StreamSearch to persist.
     */
    public void persist(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final StreamSearch inResource)
    {
        persistStreamSearch(inActionContext, inFields, inResource);
    }

    /**
     * Create set of keywords based on user input.
     *
     * @param inFields
     *            StreamSearch parameters.
     * @return Set of keywords based on user input.
     */
    protected Set<String> getKeywords(final Map<String, Serializable> inFields)
    {
        String[] keywords = ((String) inFields.get("keywords")).split(",");
        for (int i = 0; i < keywords.length; i++)
        {
            keywords[i] = keywords[i].trim();
        }
        return new HashSet<String>(Arrays.asList(keywords));
    }

    /**
     * Get the StreamView object associated with this StreamSearch.
     *
     * @param inFields
     *            StreamSearch parameters.
     * @return The StreamView object associated with this StreamSearch.
     */
    protected StreamView getStreamView(final Map<String, Serializable> inFields)
    {
        StreamView sv = streamViewDAO.execute(new FindByIdRequest(StreamView.getDomainEntityName(), ((Long) inFields
                .get("streamViewId")).longValue()));
        return sv;
    }

    /**
     * Get the name associated with this StreamSearch.
     *
     * @param inFields
     *            StreamSearch parameters.
     * @return The name associated with this StreamSearch.
     */
    protected String getName(final Map<String, Serializable> inFields)
    {
        return ((String) inFields.get("name")).trim();
    }

    /**
     * @return the streamViewDAO
     */
    protected FindByIdMapper<StreamView> getStreamViewDAO()
    {
        return streamViewDAO;
    }

    /**
     * @return the personDAO
     */
    protected FindByIdMapper<Person> getPersonDAO()
    {
        return personDAO;
    }
}
