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

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.action.request.gallery.CompressGadgetZoneRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Gets the visible gadgets for a given tab template zone.
 */
public class GetVisibleGadgetsInZone extends BaseArgDomainMapper<CompressGadgetZoneRequest, List<Gadget>>
{
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Gadget> execute(final CompressGadgetZoneRequest inRequest)
    {
        Query query = getEntityManager()
                .createQuery(
                        "FROM Gadget WHERE template.id = :tabTemplateId "
                                + "AND zoneNumber = :zoneNumber AND deleted = false ORDER BY zoneIndex")
                .setParameter("tabTemplateId", inRequest.getTabTemplateId())
                .setParameter("zoneNumber", inRequest.getZoneNumber());
        return query.getResultList();
    }
}
