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
package org.eurekastreams.server.service.opensocial.gadgets.spec;

import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.GeneralGadgetDefinition;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;

/**
 * Interface to represent the Gadget MetaData Fetching algorithm.
 */
public interface GadgetMetaDataFetcher
{
    /**
     * Method for retrieving gadget metadata.
     * @param inGadgetDefinitions - Map of gadgetdef url keys and GadgetDefinition values.
     * @return - list of GadgetMetaDataDTO objects.
     * @throws Exception on error.
     */
    List<GadgetMetaDataDTO> getGadgetsMetaData(Map<String, GeneralGadgetDefinition> inGadgetDefinitions)
    throws Exception;
}
