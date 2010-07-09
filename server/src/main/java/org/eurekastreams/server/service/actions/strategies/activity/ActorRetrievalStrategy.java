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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Interface that describes the strategy for retrieving an actor from an Activity Action request.
 *
 */
public interface ActorRetrievalStrategy
{
    /**
     * Retrieve the Actor's account id from the system.
     *
     * @param inUser
     *            - Principal that represents the user making the request.
     * @param inActivity
     *            - ActivityDTO instance for the Activity request.
     * @return - String account id of the actor of the activity.
     * @throws Exception
     *             - on error.
     */
    String getActorAccountId(Principal inUser, ActivityDTO inActivity) throws Exception;

    /**
     * Retrieve the Actor's Entity Id from the system. This is the db id.
     *
     * @param inUser
     *            - Principal that represents the user making the request.
     * @param inActivity
     *            - ActivityDTO instancefor the Activity request.
     * @return - Long entity id of the actor of the activity.
     * @throws Exception
     *             - on error.
     */
    Long getActorId(Principal inUser, ActivityDTO inActivity) throws Exception;

    /**
     * Retrieve the EntityType of the Actor.
     *
     * @return - EntityType of the Actor.
     */
    EntityType getEntityType();
}
