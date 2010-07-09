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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateGadgetsWithNewTabLayoutRequest;

/**
 * This mapper is responsible for updating the gadgets within a layout that is being changed. This update applies for
 * gadgets in zones that will no longer exist in the new layout.
 *
 */
public class UpdateGadgetsWithNewTabLayoutMapper extends
        BaseArgDomainMapper<UpdateGadgetsWithNewTabLayoutRequest, Object>
{

    /**
     * {@inheritDoc}.
     *
     * This method performs the updates that include clearing out any deleted gadgets for this tab template and moving
     * any gadgets in zones outside of the boundaries of the new layout into the last column of the new layout.
     *
     * @return null.
     */
    @Override
    public Object execute(final UpdateGadgetsWithNewTabLayoutRequest inRequest)
    {
        // Clear out deleted gadgets. Changing a layout will clear this history.
        getEntityManager().createQuery("delete from Gadget where template.id = :tabTemplateId and deleted is true")
                .setParameter("tabTemplateId", inRequest.getTabTemplateId()).executeUpdate();

        // Retrieve the gadgets that are ourside the bounds of the new layout.
        Query gadgetIdsToBeMovedQuery = getEntityManager().createQuery(
                "select id from Gadget where zoneNumber > :maxZoneNumber and "
                        + "template.id = :tabTemplateId order by zoneNumber, zoneIndex").setParameter("maxZoneNumber",
                inRequest.getNewLayout().getNumberOfZones() - 1).setParameter("tabTemplateId",
                inRequest.getTabTemplateId());
        List<Long> gadgetIdsToBeMoved = gadgetIdsToBeMovedQuery.getResultList();

        if (gadgetIdsToBeMoved.size() > 0)
        {
            // Find the last index of the last zone in the new layout.
            Query maxGadgetZoneIndexQuery = getEntityManager().createQuery(
                    "select max(zoneIndex) from Gadget "
                            + "where zoneNumber = :maxZoneNumber and template.id = :tabTemplateId").setParameter(
                    "maxZoneNumber", inRequest.getNewLayout().getNumberOfZones() - 1).setParameter("tabTemplateId",
                    inRequest.getTabTemplateId());
            Integer lastIndexLastZone = (Integer) maxGadgetZoneIndexQuery.getSingleResult();

            //If the target zone for the moved gadgets is empty, the previous query will return null.  So
            //the starting index will be set to 0.
            if (lastIndexLastZone == null)
            {
                lastIndexLastZone = 0;
            }
            //Otherwise increase the index by one and that is where the gadgets will begin to be added.
            else
            {
                lastIndexLastZone++;
            }

            // Update the gadgets outside of the bounds of the new layout with new indexes.
            for (int index = 0; index < gadgetIdsToBeMoved.size(); index++)
            {
                getEntityManager()
                        .createQuery(
                            "update versioned Gadget "
                             + "set zoneNumber = :maxZoneNumber, zoneIndex = :currentZoneIndex where id = :gadgetId")
                        .setParameter("maxZoneNumber", inRequest.getNewLayout().getNumberOfZones() - 1).setParameter(
                                "currentZoneIndex", lastIndexLastZone + index).setParameter("gadgetId",
                                gadgetIdsToBeMoved.get(index)).executeUpdate();
            }
        }

        getEntityManager().clear();

        return null;
    }

}
