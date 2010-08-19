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
package org.eurekastreams.server.persistence.mappers.opensocial;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.ReadMapper;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.GetGadgetsByGadgetDefAndConsumerKeyRequest;

/**
 * This mapper retrieves a count of the gadgets for a user based on the passed in OAuthConsumer key. This mapper
 * provides an authorization check to verify that a request made from a gadget is authorized for a user that owns that
 * gadget.
 * 
 */
public class GetGadgetsByGadgetDefAndConsumerKey extends ReadMapper<GetGadgetsByGadgetDefAndConsumerKeyRequest, Long>
{
    /**
     * {@inheritDoc}. This mapper retrieves a count of 1 if the user passed in with this request has an instance of the
     * app associated with the consumerkey supplied in the request.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Long execute(final GetGadgetsByGadgetDefAndConsumerKeyRequest inRequest)
    {
        String consumerKey = inRequest.getConsumerKey();
        Long personId = inRequest.getPersonId();
        Query gadgetDefIdQuery = getEntityManager().createQuery(
                "select gd.id from GadgetDefinition gd, OAuthConsumer oc "
                        + "where gd.url = oc.gadgetUrl and oc.consumerKey =:consumerKey").setParameter("consumerKey",
                consumerKey);
        
        List<Long> results = gadgetDefIdQuery.getResultList();
        
        if (results.size() == 0)
        {
            return 0L;
        }
        
        Long gadgetDefId = (Long) results.get(0);
        Query gadgetCountQuery = getEntityManager().createQuery(
                "select count(ga.id) from Gadget ga, Tab tab, Person p "
                        + "where ga.template.id = tab.template.id and p.startTabGroup.id = tab.tabGroup.id "
                        + "and p.id =:personId and ga.deleted = 'f' and ga.gadgetDefinition.id =:gadgetDefId")
                .setParameter("personId", personId).setParameter("gadgetDefId", gadgetDefId);
        return (Long) gadgetCountQuery.getSingleResult();
    }
}
