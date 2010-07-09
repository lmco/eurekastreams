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
package org.eurekastreams.web.client.ui.common.stream.decorators;

import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Interface for an activityDTO populator strategy.
 * 
 */
public interface ActivityDTOPopulatorStrategy
{
    /**
     * Populates an ActivityDTO with properties.
     * 
     * @param activity
     *            the dto.
     */
    void populate(final ActivityDTO activity);
}
