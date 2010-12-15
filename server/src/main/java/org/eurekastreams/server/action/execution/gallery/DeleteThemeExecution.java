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
package org.eurekastreams.server.action.execution.gallery;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Action to delete theme and queue up deletion of associated cache keys upon success.
 * 
 */
public class DeleteThemeExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Find theme by id mapper.
     */
    private final DomainMapper<FindByIdRequest, Theme> findByIdMapper;

    /** Mapper to delete the gadget definition. */
    private final DomainMapper<Long, Void> deleteThemeMapper;

    /** Mapper to get list of affected tab templates. */
    private final DomainMapper<Long, Collection<Long>> getPeopleIdsUsingTheme;

    /** Name of action to initiate. */
    private final String deleteCacheKeysActionName;

    /**
     * Constructor.
     * 
     * @param inFindByIdMapper
     *            Find by id mapper for theme.
     * 
     * @param inDeleteThemeMapper
     *            Mapper to delete the Theme.
     * @param inGetPeopleIdsUsingTheme
     *            Mapper to get list of affected people.
     * @param inDeleteCacheKeysActionName
     *            Name of action to initiate.
     */
    public DeleteThemeExecution(final DomainMapper<FindByIdRequest, Theme> inFindByIdMapper,
            final DomainMapper<Long, Void> inDeleteThemeMapper,
            final DomainMapper<Long, Collection<Long>> inGetPeopleIdsUsingTheme,
            final String inDeleteCacheKeysActionName)
    {
        findByIdMapper = inFindByIdMapper;
        deleteThemeMapper = inDeleteThemeMapper;
        getPeopleIdsUsingTheme = inGetPeopleIdsUsingTheme;
        deleteCacheKeysActionName = inDeleteCacheKeysActionName;
    }

    /**
     * Delete theme from DB and queue up associated cache keys to be deleted upon success.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return null.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        // get theme info needed.
        Long themeId = (Long) inActionContext.getActionContext().getParams();
        Theme theme = findByIdMapper.execute(new FindByIdRequest("Theme", themeId));
        String themeUuid = theme.getUUID();

        // Create list of cacheKeys to delete, initialized with the two keys for the theme being deleted.
        HashSet<String> cacheKeysToDelete = new HashSet<String>(Arrays.asList(CacheKeys.THEME_CSS_BY_UUID + themeUuid,
                CacheKeys.THEME_HASH_BY_UUID + themeUuid));

        // add PersonPagePropertiesDTO cache keys for all users that were configured with that theme
        // as they will have null theme (default) after theme is deleted.
        Collection<Long> personIds = getPeopleIdsUsingTheme.execute(themeId);
        for (Long id : personIds)
        {
            cacheKeysToDelete.add(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID + id);
        }

        // queue up request to delete cache keys.
        inActionContext.getUserActionRequests().add(
                new UserActionRequest(deleteCacheKeysActionName, null, cacheKeysToDelete));

        // delete the gadget
        deleteThemeMapper.execute(themeId);

        return null;
    }
}
