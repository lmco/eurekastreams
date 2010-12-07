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
package org.eurekastreams.server.domain.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO with a person's open social id, gadget definition id, and key-value pairs for a gadget, used for caching to avoid
 * database hits.
 */
public class AppDataDTO implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -8804197553460346537L;

    /**
     * Person's open social id.
     */
    private String openSocialId;

    /**
     * Gadget definition id.
     */
    private long gadgetDefinitionId;

    /**
     * Map of key/value pairs.
     */
    private Map<String, String> keyValuePairs;

    /**
     * @return the openSocialId
     */
    public String getOpenSocialId()
    {
        return openSocialId;
    }

    /**
     * Constructor.
     * 
     * @param inOpenSocialId
     *            the open social id of the person
     * @param inGadgetDefinitionId
     *            the gadget definition id
     * @param inKeyValuePairs
     *            the key/value pairs for the person/gadget
     */
    public AppDataDTO(final String inOpenSocialId, final long inGadgetDefinitionId,
            final Map<String, String> inKeyValuePairs)
    {
        super();
        openSocialId = inOpenSocialId;
        gadgetDefinitionId = inGadgetDefinitionId;
        keyValuePairs = inKeyValuePairs;
    }

    /**
     * @param inOpenSocialId
     *            the openSocialId to set
     */
    public void setOpenSocialId(final String inOpenSocialId)
    {
        openSocialId = inOpenSocialId;
    }

    /**
     * @return the gadgetDefinitionId
     */
    public long getGadgetDefinitionId()
    {
        return gadgetDefinitionId;
    }

    /**
     * @param inGadgetDefinitionId
     *            the gadgetDefinitionId to set
     */
    public void setGadgetDefinitionId(final long inGadgetDefinitionId)
    {
        gadgetDefinitionId = inGadgetDefinitionId;
    }

    /**
     * @return the keyValuePairs
     */
    public Map<String, String> getKeyValuePairs()
    {
        return keyValuePairs;
    }

    /**
     * @param inKeyValuePairs
     *            the keyValuePairs to set
     */
    public void setKeyValuePairs(final Map<String, String> inKeyValuePairs)
    {
        keyValuePairs = inKeyValuePairs;
    }

}
