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
package org.eurekastreams.server.search.modelview;

import java.io.Serializable;

/**
 * Gadget definition DTO.
 * 
 */
public class GadgetDefinitionDTO implements Serializable
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -8576747072638496373L;

    /**
     * GadgetDefinition id.
     */
    private long id;

    /**
     * Storage for the url that describes the location of the gadget definition.
     */
    private String url;

    /**
     * UUID associated with the theme as a string.
     */
    private String uuid;

    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param inId
     *            the id to set
     */
    public void setId(final long inId)
    {
        id = inId;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param inUrl
     *            the url to set
     */
    public void setUrl(final String inUrl)
    {
        url = inUrl;
    }

    /**
     * @return the uuid
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * @param inUuid
     *            the uuid to set
     */
    public void setUuid(final String inUuid)
    {
        uuid = inUuid;
    }

}
