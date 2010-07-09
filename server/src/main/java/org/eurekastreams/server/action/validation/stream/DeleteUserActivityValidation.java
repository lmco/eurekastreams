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
package org.eurekastreams.server.action.validation.stream;

import java.util.List;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;

/**
 * Validate that principal is actor on activities that are requested for delete. If not, the id requested for delete is
 * removed from the list before going to execution strategy.
 * 
 */
public class DeleteUserActivityValidation implements ValidationStrategy<PrincipalActionContext>
{
    /**
     * {@link BulkActivitiesMapper}.
     */
    private BulkActivitiesMapper activityMapper;

    /**
     * Constructor.
     * 
     * @param inActivityMapper
     *            {@link BulkActivitiesMapper}.
     */
    public DeleteUserActivityValidation(final BulkActivitiesMapper inActivityMapper)
    {
        activityMapper = inActivityMapper;
    }

    /**
     * Validate that principal is actor on activities that are requested for delete. If not, the id requested for delete
     * is removed from the list.
     * 
     * @param inActionContext
     *            the PrincipalActionContent.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void validate(final PrincipalActionContext inActionContext)
    {
        // NOTE: This is using BulkActivitiesMapper in hopes of being more performant than parsing out ids via querying
        // the db as activities can be fetched from cache and checked, but there is no hard evidence that this
        // is the case. Will have to rely on perf testing to make the call, but since this isn't really being used
        // currently there's not much need to invest a lot of time in determining the actual numbers.

        List<Long> activityIds = (List<Long>) inActionContext.getParams();
        Long principalId = inActionContext.getPrincipal().getId();

        List<ActivityDTO> activities = activityMapper.execute(activityIds, null);

        for (ActivityDTO activity : activities)
        {
            if (activity.getActor().getId() != principalId)
            {
                activityIds.remove(activity.getId());
            }
        }
    }
}
