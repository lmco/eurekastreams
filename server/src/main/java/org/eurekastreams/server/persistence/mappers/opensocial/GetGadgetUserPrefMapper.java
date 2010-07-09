/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.opensocial;

import javax.persistence.Query;

import org.eurekastreams.server.domain.GadgetUserPrefDTO;
import org.eurekastreams.server.persistence.mappers.ReadMapper;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.GadgetUserPrefRequest;

/**
 * Mapper for retrieving Gadget User Preferences.
 *
 */
public class GetGadgetUserPrefMapper extends 
    ReadMapper<GadgetUserPrefRequest, GadgetUserPrefDTO>
{

    /**
     * Execute method that actually retrieves the GadgetUserPref object.
     * @param inRequest - GadgetUserPref Request object containing the data
     * for the request.
     * @return GadgetUserPrefDTO - instance of the populated DTO object.
     */
    @Override
    public GadgetUserPrefDTO execute(final GadgetUserPrefRequest inRequest)
    {
        Query q = getEntityManager()
            .createQuery("select new org.eurekastreams.server.domain.GadgetUserPrefDTO( g.id, g.gadgetUserPref ) "
                    + "FROM Gadget g WHERE g.id =:gadgetId")
            .setParameter("gadgetId", inRequest.getGadgetId());
        
        GadgetUserPrefDTO currentUserPref = (GadgetUserPrefDTO) q.getSingleResult();
        
        return currentUserPref;
    }
}
