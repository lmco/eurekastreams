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

import java.util.HashMap;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;

/**
 * base activity DTO populator. Takes in the verb and object strategies and returns the Activity DTO.
 * 
 */
public class ActivityDTOPopulator
{
    /**
     * Default constructor.
     * 
     * @param inContent
     *            What the user typed in.
     * @param inDestinationType
     *            destination stream type.
     * @param inDestinationUniqueId
     *            destination unique id.
     * @param verbPopulator
     *            verb populator,
     * @param objectPopulator
     *            object populator.
     * @return the activity DTO.
     */
    public ActivityDTO getActivityDTO(final String inContent, final EntityType inDestinationType,
            final String inDestinationUniqueId, final ActivityDTOPopulatorStrategy verbPopulator,
            final ActivityDTOPopulatorStrategy objectPopulator)
    {
        ActivityDTO activity = new ActivityDTO();
        HashMap<String, String> props = new HashMap<String, String>();
        activity.setBaseObjectProperties(props);
        activity.getBaseObjectProperties().put("content", inContent);

        StreamEntityDTO destination = new StreamEntityDTO();
        destination.setUniqueIdentifier(inDestinationUniqueId);
        destination.setType(inDestinationType);
        activity.setDestinationStream(destination);

        if (verbPopulator != null)
        {
            verbPopulator.populate(activity);
        }
        if (objectPopulator != null)
        {
            objectPopulator.populate(activity);

        }

        return activity;
    }
}
