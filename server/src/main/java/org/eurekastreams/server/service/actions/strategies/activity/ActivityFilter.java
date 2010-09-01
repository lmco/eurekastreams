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
package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * The interface for any filter to be applied to a list of activities.
 */
public interface ActivityFilter
{

    /**
     * Apply the filter.
     * 
     * @param activities
     *            list of activities to filter.
     * @param currentUserAccount
     *            Account for the user currently logged into the system.
     */
    void filter(final List<ActivityDTO> activities, PersonModelView currentUserAccount);
}
