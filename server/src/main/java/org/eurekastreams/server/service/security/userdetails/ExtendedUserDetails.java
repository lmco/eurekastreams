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
package org.eurekastreams.server.service.security.userdetails;

import org.eurekastreams.server.domain.PersistentLogin;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.springframework.security.userdetails.UserDetails;

/**
 * Interface that extends Spring's UserDetails interface to allow access to
 * Person and PersistentLogin object.
 *
 */
public interface ExtendedUserDetails extends UserDetails
{
    /**
     * Getter for Person object.
     *
     * @return the Person object if available, null if not.
     */
    Person getPerson();

    /**
     * Getter for PersistentLoginObject.
     *
     * @return the PersistentLogin information if available, null if not.
     */
    PersistentLogin getPersistentLogin();

    /**
     * Get Authentication type for user.
     * @return Authentication type for user.
     */
    AuthenticationType getAuthenticationType();
}
