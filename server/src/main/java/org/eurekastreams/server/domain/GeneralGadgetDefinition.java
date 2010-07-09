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
package org.eurekastreams.server.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * General interface for an object that defines a gadget.
 *
 */
public interface GeneralGadgetDefinition extends GalleryItem, Serializable
{
    /**
     * set the url.
     * 
     * @param inUrl
     *            The URL of the gallery item.
     */
    void setUrl(final String inUrl);
    
    /**
     * @return the URL.
     */
    String getUrl();
    
    /**
     * @return the UUID.
     */
    String getUUID();
    
    /**
     * 
     * @return the ID
     */
    long getId();
    
    /**
     * Needed for serialization.
     * 
     * @param inUUID
     *            UUID to use.
     */
    void setUUID(final String inUUID);

    /**
     * 
     * @return the number of users
     */
    int getNumberOfUsers();

    /**
     * 
     * @return returns created date
     */
    Date getCreated();
    
    /**
     * @param inCreated the created date to set.
     */
    void setCreated(Date inCreated);
}
