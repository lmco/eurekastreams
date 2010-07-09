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
package org.eurekastreams.commons.actions.context;

import java.io.Serializable;

/**
 * This interface describes the user context of an action execution.
 * The Principal is supplied by the implementation of the supplied {@link PrincipalPopulator}.
 *
 */
public interface Principal extends Serializable
{
    /**
     * Retrieve the String based account id of the user.
     * @return - String based account id.
     */
    String getAccountId();

    /**
     * Retrieve the String that represents the OpenSocial id for the current user.
     * @return - String based OpenSocial id.
     */
    String getOpenSocialId();

    /**
     * Retrieve the Long that represents the current user within persistent storage.
     * @return - Long based entity id.
     */
    Long getId();
}
