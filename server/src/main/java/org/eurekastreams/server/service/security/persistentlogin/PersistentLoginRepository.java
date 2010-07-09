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
package org.eurekastreams.server.service.security.persistentlogin;

import org.eurekastreams.server.domain.PersistentLogin;

/**
 * Interface for PersistentLogin data store. Methods here are where @Transactional
 * is used if required as it cannot be placed in PersistentLoginService.
 * 
 */
public interface PersistentLoginRepository
{
    /**
     * Creates or updates PersistentLogin info in data store.
     * 
     * @param persistentLogin
     *            The info to store.
     */
    void createOrUpdatePersistentLogin(PersistentLogin persistentLogin);

    /**
     * Returns PersistentLogin record from data store associated with specific
     * user or null if one not present.
     * 
     * @param username
     *            The username.
     * @return PersistentLogin record from data store associated with specific
     *         user or null if one not present.
     */
    PersistentLogin getPersistentLogin(String username);

    /**
     * Removes all PersistentLogin info associated with a specific user from
     * data store.
     * 
     * @param username
     *            The username.
     */
    void removePersistentLogin(String username);

}
