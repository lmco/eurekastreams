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
 * Interface used to get ActivityDTO from params array. Used in different Authorization objects that apply to actions
 * that have different array contents.
 *
 * @param <ParamType>
 *            the type of parameter the execute takes
 */
public interface ActivityDTOFromParamsStrategy<ParamType extends Serializable>
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
    ActivityDTO execute(Principal inUser, ParamType inParams);
}
