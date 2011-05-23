/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import java.util.List;
import java.util.Set;

/**
 * Entity that container other entities.
 */
public interface CompositeEntity
{
    /**
     * @return the capabilities
     */
    List<BackgroundItem> getCapabilities();

    /**
     * @param inCapabilities
     *            the capabilities to set
     */
    void setCapabilities(final List<BackgroundItem> inCapabilities);

    /**
     * @return the entities coordinators.
     */
    Set<Person> getCoordinators();

    /**
     * Setter for overview.
     * 
     * @param overview
     *            the overview.
     */
    void setOverview(String overview);

    /**
     * Getter for overview.
     * 
     * @return the overview.
     */
    String getOverview();

    /** Used for validation. */
    String URL_REGEX_PATTERN = "^(https?://)" + "(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"// user@
            + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP 199.194.52.184
            + "|" // allow IP or Domain
            + "([0-9a-zA-Z_!~*'()-]+\\.)*" // tertiary domain(s) www.
            + "([0-9a-zA-Z_][0-9a-zA-Z-_]{0,61})?[0-9a-zA-Z_]\\." // second level domain
            + "[a-zA-Z]{2,6})" // first level domain - .com or .museum
            + "(:[0-9]{1,4})?" // port number :80
            + "((/?)|" // slash not required if no file name.
            + "(/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)$";

}
