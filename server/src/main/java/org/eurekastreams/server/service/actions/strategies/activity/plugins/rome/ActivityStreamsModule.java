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
package org.eurekastreams.server.service.actions.strategies.activity.plugins.rome;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * ActivityStreamsModule interface, required for ROME.
 * 
 */
public interface ActivityStreamsModule extends Module
{
    /**
     * URI for the ActivityStreams namespace.
     */
    String URI = "http://activitystrea.ms/spec/1.0/";

    /**
     * Gets the object type.
     * 
     * @return the object type.
     */
     String getObjectType();

    /**
     * Sets the object type.
     * 
     * @param inType
     *            the object type.
     */
     void setObjectType(String inType);

    /**
     * Gets the atom entry.
     * 
     * @return the atom entry.
     */
     SyndEntryImpl getAtomEntry();

    /**
     * sets the atom entry.
     * 
     * @param inAtomEntry
     *            the atom entry.
     */
     void setAtomEntry(SyndEntryImpl inAtomEntry);
}
