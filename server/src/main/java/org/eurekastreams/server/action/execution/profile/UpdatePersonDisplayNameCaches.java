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
package org.eurekastreams.server.action.execution.profile;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.CacheUpdater;

/**
 * Strategy to update a person's display name in Activity and Comment caches.
 */
public class UpdatePersonDisplayNameCaches implements CacheUpdater
{
    /**
     * The name of the person display name updater async action - activities and comments authored by the person with
     * the changing name.
     */
    private static final String ASYNC_ACTION_NAME = "personDisplayNameUpdaterAsyncAction";

    /**
     * The name of the action to call to update the stream name of activities posted to a person stream authored by the
     * person with the changing name.
     */
    private static final String ASYNC_PERSONAL_STREAM_POSTS_ACTION_NAME
    // line break
    = "activityRecipientPersonNameUpdaterAsyncAction";

    /**
     * The name of the action to kick off that updates notifications.
     */
    private static final String NOTIFICATION_UPDATE_ACTION_NAME = "updateNotificationsOnPersonNameChange";

    /**
     * Mapper to get people by ids.
     */
    private GetPeopleByIds getPeopleByIdsMapper;

    /**
     * Constructor.
     * 
     * @param inGetPeopleByIdsMapper
     *            mapper to get people by ids.
     */
    public UpdatePersonDisplayNameCaches(final GetPeopleByIds inGetPeopleByIdsMapper)
    {
        getPeopleByIdsMapper = inGetPeopleByIdsMapper;
    }

    /**
     * Return list of UserActionRequests to update the cache.
     * 
     * @param inUser
     *            the {@link Principal} executing the operation.
     * @param inPersonId
     *            The id of the person being updated.
     * @return list of UserActionRequests.
     */
    @Override
    public List<UserActionRequest> getUpdateCacheRequests(final Principal inUser, final Long inPersonId)
    {
        List<UserActionRequest> results = new ArrayList<UserActionRequest>(3);
        results.add(new UserActionRequest(ASYNC_ACTION_NAME, null, inPersonId));
        results.add(new UserActionRequest(NOTIFICATION_UPDATE_ACTION_NAME, null, inPersonId));

        // the next async action needs the account id
        PersonModelView person = getPeopleByIdsMapper.execute(inPersonId);

        results.add(new UserActionRequest(ASYNC_PERSONAL_STREAM_POSTS_ACTION_NAME, null, person.getAccountId()));
        return results;
    }
}
