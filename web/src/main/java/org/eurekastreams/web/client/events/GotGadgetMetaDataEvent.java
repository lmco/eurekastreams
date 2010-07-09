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
package org.eurekastreams.web.client.events;

import java.util.List;

import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;

/**
 * Got the gadget meta data. Gets thrown after Shindig returns
 * with the gadget metadata and the fetcher assembles the object.
 *
 */
public class GotGadgetMetaDataEvent
{
    /**
     * The gadget metadata.
     */
    private List<GadgetMetaDataDTO> metadata;

    /**
     * Default constructor.
     * @param inMetaData the metadata.
     */
    public GotGadgetMetaDataEvent(final List<GadgetMetaDataDTO> inMetaData)
    {
        metadata = inMetaData;
    }

    /**
     * Get the metadata.
     * @return the metadata.
     */
    public List<GadgetMetaDataDTO> getMetadata()
    {
        return metadata;
    }
}
