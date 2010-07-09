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

import java.io.Serializable;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Strategy for returning DTO directly from params array.
 *
 */
public class ActivityDTOFromParamsStrategyDirect implements ActivityDTOFromParamsStrategy<Serializable[]>
{
    /**
     * Return ActivityDTO based on inParamsContent.
     *
     * @param inUser
     *            user making the request
     * @param inParams
     *            parameters to get ActivityDTO based on.
     * @return ActivityDTO.
     */
    public ActivityDTO execute(final Principal inUser, final Serializable[] inParams)
    {
        return (ActivityDTO) inParams[0];
    }
}
