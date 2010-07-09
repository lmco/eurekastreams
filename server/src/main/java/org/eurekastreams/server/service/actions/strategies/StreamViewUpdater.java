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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.stream.RefreshCachedCompositeStreamRequest;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindUserStreamViewById;
import org.eurekastreams.server.persistence.mappers.GetStreamScopeProxyById;
import org.eurekastreams.server.persistence.mappers.cache.AddCachedActivityToListByStreamScope;
import org.eurekastreams.server.persistence.mappers.cache.GetCompositeStreamById;
import org.eurekastreams.server.persistence.mappers.cache.RemoveCachedActivitiesFromListByStreamScope;
import org.eurekastreams.server.persistence.mappers.requests.AddCachedActivityToListByStreamScopeRequest;
import org.eurekastreams.server.persistence.mappers.requests.FindUserStreamFilterByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.RemoveCachedActivitiesFromListByStreamScopeRequest;
import org.eurekastreams.server.persistence.mappers.stream.UpdateCachedCompositeStream;

/**
 * Strategy for updating a {@link StreamView}.
 *
 */
public class StreamViewUpdater implements ResourcePersistenceStrategy<StreamView>, UpdaterStrategy
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.getLog(StreamViewUpdater.class);

    /**
     * The update cached composite stream mapper.
     */
    private final UpdateCachedCompositeStream updateCachedCompositeStream;

    /**
     * Find by id DAO for {@link StreamView}.
     */
    private final FindUserStreamViewById streamViewDAO;

    /**
     * GetStreamScopeProxyById DAO.
     */
    private final GetStreamScopeProxyById streamScopeProxyDAO;

    /**
     * CompositeStream mapper.
     */
    private final GetCompositeStreamById streamsMapper;

    /**
     * AddCachedActivityToListByStreamScope mapper. Used for updating cached activities in lists.
     */
    private final AddCachedActivityToListByStreamScope addCachedActivityMapper;

    /**
     * RemoveCachedActivitiesFromListByStreamScope mapper. Used for removing cached activities in lists.
     */
    private final RemoveCachedActivitiesFromListByStreamScope removeCachedActivitiesMapper;

    /**
     * Constructor.
     *
     * @param inUpdateCachedCompositeStream
     *            The update cached composite stream mapper.
     * @param inStreamViewDAO
     *            Find by id DAO for {@link StreamView}.
     * @param inStreamScopeProxyDAO
     *            the GetStreamScopeProxyById dao.
     * @param inStreamsMapper
     *            mapper to retrieve the existing cached StreamView for performing cache updates.
     * @param inAddCachedActivityMapper
     *            mapper used to update the current StreamView's activities in cache with each of the additional scopes.
     * @param inRemoveCachedActivitiesMapper
     *            mapper used to remove activities from the current StreamView in cache with scopes that were removed
     *            from the ui.
     */
    public StreamViewUpdater(final UpdateCachedCompositeStream inUpdateCachedCompositeStream,
            final FindUserStreamViewById inStreamViewDAO, final GetStreamScopeProxyById inStreamScopeProxyDAO,
            final GetCompositeStreamById inStreamsMapper,
            final AddCachedActivityToListByStreamScope inAddCachedActivityMapper,
            final RemoveCachedActivitiesFromListByStreamScope inRemoveCachedActivitiesMapper)
    {
        updateCachedCompositeStream = inUpdateCachedCompositeStream;
        streamViewDAO = inStreamViewDAO;
        streamScopeProxyDAO = inStreamScopeProxyDAO;
        streamsMapper = inStreamsMapper;
        addCachedActivityMapper = inAddCachedActivityMapper;
        removeCachedActivitiesMapper = inRemoveCachedActivitiesMapper;
    }

    /**
     * Gets a resource.
     *
     * @param inActionContext
     *            action context
     * @param inFields
     *            the resources fields.
     * @return the resource.
     */
    public StreamView get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        return streamViewDAO.execute(new FindUserStreamFilterByIdRequest(inActionContext.getActionContext()
                .getPrincipal().getId(), ((Long) inFields.get("id")).longValue()));
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
     * @throws Exception
     *             On error.
     */
    public void persist(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final StreamView inResource) throws Exception
    {
        // Retrieve the current streamview from cache
        // This is where the stream scopes will be retrieved
        // and compared to the stream scopes in the updated resource.
        // This needs to be done before the DAO is flushed because
        // if the streamview isn't found in cache, the mapper will
        // look to the db and we don't want to retrieve the updated
        // streamview or that would kill all comparisons.

        StreamView streamViewResult = streamsMapper.execute(inResource.getId());
        List<StreamScope> removedScopes = new ArrayList<StreamScope>();
        List<StreamScope> addedScopes = new ArrayList<StreamScope>();
        if (streamViewResult != null)
        {
            Set<StreamScope> currentScopes = streamViewResult.getIncludedScopes();
            Set<StreamScope> targetScopes = inResource.getIncludedScopes();

            // Find the scopes that have been removed.
            // Loop through existing scopes and compare to new set of scopes
            // to find those existing scopes that don't exist in the new set.
            for (StreamScope currentScope : currentScopes)
            {
                if (!targetScopes.contains(currentScope))
                {
                    // this scope has been removed.
                    removedScopes.add(currentScope);
                }
            }
            // Find the scopes that have been added.
            // Loop through new scopes and compare to existing scopes
            // to find those new scopes that were added.
            for (StreamScope targetScope : targetScopes)
            {
                if (!currentScopes.contains(targetScope))
                {
                    // this scope has been added.
                    addedScopes.add(targetScope);
                }
            }

            // Make sure a scope hasn't been removed, then readded to the list.
            // This would cause unnecessary execution.
            List<StreamScope> noopScopes = new ArrayList<StreamScope>();
            for (StreamScope currentAddedScope : addedScopes)
            {
                if (removedScopes.contains(currentAddedScope))
                {
                    noopScopes.add(currentAddedScope);
                }
            }
            removedScopes.removeAll(noopScopes);
            addedScopes.removeAll(noopScopes);
        }

        streamViewDAO.flush();

        updateCachedCompositeStream.execute(inResource.getId());

        // Update the cached activities for this particular list.
        Long userId = inActionContext.getActionContext().getPrincipal().getId();
        if (removedScopes.size() > 0)
        {
            RemoveCachedActivitiesFromListByStreamScopeRequest request;
            // call remove mapper here
            for (StreamScope currentStreamScope : removedScopes)
            {
                request = new RemoveCachedActivitiesFromListByStreamScopeRequest(inResource.getId(), userId,
                        currentStreamScope);
                removeCachedActivitiesMapper.execute(request);
            }
        }
        if (addedScopes.size() > 0)
        {
            AddCachedActivityToListByStreamScopeRequest request;
            RefreshCachedCompositeStreamRequest actionRequest;
            // call add mapper here
            for (StreamScope currentStreamScope : addedScopes)
            {
                request = new AddCachedActivityToListByStreamScopeRequest(inResource.getId(), userId,
                        currentStreamScope);
                addCachedActivityMapper.execute(request);

                // Post an async action to update the entire cached list.
                actionRequest = new RefreshCachedCompositeStreamRequest(inResource.getId(), userId);

                log.info("Queuing a refreshCacheCustomCompositeStreamAction task for the task queue.");
                inActionContext.getUserActionRequests().add(
                        new UserActionRequest("refreshCachedCustomCompositeStreamAction", null, actionRequest));
            }
        }
    }

    /**
     * The method that will set properties on an instance.
     *
     * @param instance
     *            target on which to set values.
     * @param properties
     *            the key/value map for which properties to set to what.
     */
    @SuppressWarnings("unchecked")
    public void setProperties(final Object instance, final Map<String, Serializable> properties)
    {
        StreamView streamView = (StreamView) instance;

        if (properties.containsKey("name"))
        {
            streamView.setName((String) properties.get("name"));
        }

        if (properties.containsKey("scopes"))
        {
            List<StreamScope> targetScopes = (List<StreamScope>) properties.get("scopes");
            Set<StreamScope> currentScopes = streamView.getIncludedScopes();

            log.debug("before scope synch size: " + currentScopes.size());
            currentScopes.clear();
            for (StreamScope targetScope : targetScopes)
            {
                // streamScopeProxyDAO is not actually doing a fetch, so this
                // isn't
                // hitting the DB for StreamScopes.
                currentScopes.add(streamScopeProxyDAO.execute(targetScope.getId()));
            }
            log.debug("after scope synch size: " + currentScopes.size());
        }
    }
}
