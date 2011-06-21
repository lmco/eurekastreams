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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.Collection;

import javax.persistence.Query;

import org.eurekastreams.server.action.request.gallery.CompressGadgetZoneRequest;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Gets the list of tab template zones that contain instances of a given gadget definition with the tabs' owners.
 */
public class GetZonesToCompressForGadgetDefinition extends
        BaseArgDomainMapper<Long, Collection<CompressGadgetZoneRequest>>
{
    /**
     * @see GetZonesToCompressForGadgetDefinition
     * @param inRequest
     *            ID of gadget definition.
     * @return List of requests to fan out to the compress gadget zone action.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<CompressGadgetZoneRequest> execute(final Long inRequest)
    {
        Query query = getEntityManager().createQuery(
                "SELECT NEW org.eurekastreams.server.action.request.gallery.CompressGadgetZoneRequest"
                        + "(template.id,zoneNumber,owner.id) FROM Gadget "
                        + "WHERE gadgetDefinition.id = :gadgetDefinitionId")
                .setParameter("gadgetDefinitionId", inRequest);
        return query.getResultList();
    }
}
