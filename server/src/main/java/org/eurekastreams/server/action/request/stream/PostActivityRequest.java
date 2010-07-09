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
package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;

import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * This class contains the request information for the PostActivityAction.
 *
 */
public class PostActivityRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -1133420640073135960L;

    /**
     * Local instance of the {@link ActivityDTO}.
     */
    private ActivityDTO activityDTO;

    /**
     * Used for Serialization.
     */
    @SuppressWarnings("unused")
    private PostActivityRequest()
    {
    }

    /**
     * Constructor for the PostActivityRequest object.
     * @param inActivityDTO - instance of the {@link ActivityDTO} to use with the action.
     */
    public PostActivityRequest(final ActivityDTO inActivityDTO)
    {
        activityDTO = inActivityDTO;
    }

    /**
     * Retrieve the current {@link ActivityDTO} instance.
     * @return - instance of the {@link ActivityDTO} object.
     */
    public ActivityDTO getActivityDTO()
    {
        return activityDTO;
    }

    /**
     * Setter for the ActivityDTO.
     * @param inActivity - activityDTO for the request.
     */
    @SuppressWarnings("unused")
	private void setActivityDTO(final ActivityDTO inActivity)
    {
        activityDTO = inActivity;
    }
}
