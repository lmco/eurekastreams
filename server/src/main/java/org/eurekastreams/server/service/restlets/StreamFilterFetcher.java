/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.restlets;

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Interface for getting stream filters.
 * 
 */
public interface StreamFilterFetcher
{

    /**
     * Gets the paged set of activities.
     * 
     * @param id
     *            the id of the filter.
     * @param openSocialId
     *            the open social id of the user.
     * @param maxCount
     *            the number of activities.
     * @return the paged set of activities.
     * @throws Exception exception.
     */
    PagedSet<ActivityDTO> getActivities(final Long id,
            final String openSocialId, final int maxCount) throws Exception;
}
