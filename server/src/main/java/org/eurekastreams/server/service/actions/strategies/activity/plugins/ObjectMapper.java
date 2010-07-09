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
package org.eurekastreams.server.service.actions.strategies.activity.plugins;

import java.util.HashMap;

import org.eurekastreams.server.domain.stream.BaseObjectType;

import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * Interface for object mapper.
 * 
 */
public interface ObjectMapper
{

    /**
     * Get the base object from the ATOM/RSS entry.
     * 
     * @param entry
     *            the entry.
     * @return the base object.
     */
    HashMap<String, String> getBaseObject(SyndEntryImpl entry);

    /**
     * Gets the base object type.
     * 
     * @return the base object type.
     */
    BaseObjectType getBaseObjectType();
}
