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
/**
 * 
 */
package org.eurekastreams.server.search.bridge;

import org.eurekastreams.server.domain.Organization;
import org.hibernate.search.bridge.TwoWayStringBridge;

/**
 * String Bridge to convert the input parent Organization to its name for query.
 */
public class OrganizationToNameFieldBridge implements TwoWayStringBridge
{
    /**
     * Convert an Organization to its name.
     * 
     * @param orgObject
     *            an Organization to transform
     * @return the name of the input Organization
     */
    @Override
    public String objectToString(final Object orgObject)
    {
        if (orgObject == null || !(orgObject instanceof Organization))
        {
            return null;
        }
        return ((Organization) orgObject).getName();
    }

    /**
     * Pass-through the input string value.
     * 
     * @param stringValue
     *            the value stored in the search index
     * @return the value stored in the search index, which is the organization's name.
     */
    @Override
    public Object stringToObject(final String stringValue)
    {
        return stringValue;
    }
}
