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
package org.eurekastreams.server.service.actions.strategies.galleryitem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.GeneralGadgetDefinition;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.service.opensocial.gadgets.spec.GadgetMetaDataFetcher;

/**
 * populates a gadget definition.
 *
 */
public class GadgetDefinitionPopulator implements GalleryItemPopulator<GadgetDefinition>
{

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GadgetDefinitionPopulator.class);

    /**
     * Meta data fetcher.
     */
    private GadgetMetaDataFetcher metaDataFetcher = null;

    /**
     * Constructor.
     *
     * @param inMetaDataFetcher
     *            meta data fetcher.
     */
    public GadgetDefinitionPopulator(final GadgetMetaDataFetcher inMetaDataFetcher)
    {
        metaDataFetcher = inMetaDataFetcher;
    }

    /**
     * Populates a gadget definition. Currently, it is an empty implementation, because there are no gadget def fields
     * that need to be populated
     *
     * @param inGadgetDefinition
     *            the gadget definition to populate
     * @param inGadgetDefinitionUrl
     *            the gadget definition url.
     */
    public void populate(final GadgetDefinition inGadgetDefinition, final String inGadgetDefinitionUrl)
    {
        final Map<String, GeneralGadgetDefinition> gadgetDefs = new HashMap<String, GeneralGadgetDefinition>();
        gadgetDefs.put(inGadgetDefinitionUrl, inGadgetDefinition);

        log.info("Fetching gadget data");

        try
        {
            List<GadgetMetaDataDTO> meta = metaDataFetcher.getGadgetsMetaData(gadgetDefs);

            if (meta.size() > 0)
            {
                /*
                 * These three fields and only these three fields are being populated because they
                 * are used for search indexes.
                 * 
                 *  Additionally the title field is updated in the data base when used by 
                 */
                
                GadgetMetaDataDTO metadata = meta.get(0);
                inGadgetDefinition.setGadgetAuthor(metadata.getAuthor());
                log.info("Got gadget author: " + inGadgetDefinition.getGadgetAuthor());

                inGadgetDefinition.setGadgetTitle(metadata.getTitle());
                log.info("Got gadget title: " + inGadgetDefinition.getGadgetTitle());

                inGadgetDefinition.setGadgetDescription(metadata.getDescription());
                log.info("Got gadget description: " + inGadgetDefinition.getGadgetDescription());
            }
        }
        catch (Exception e)
        {
            log.debug("Failed to fetch data", e);
        }
    }
}
