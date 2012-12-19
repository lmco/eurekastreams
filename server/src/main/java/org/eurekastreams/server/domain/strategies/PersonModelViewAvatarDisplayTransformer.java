/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain.strategies;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Strategy to remove fields from a PersonModelView that aren't needed for displaying an avatar. The only fields that
 * are kept are: accountId, displayName, entityId (id), avatarId. This helps cut down on the amount of data transferred
 * for lists of people.
 */
public class PersonModelViewAvatarDisplayTransformer implements
        Transformer<List<PersonModelView>, ArrayList<PersonModelView>>
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.make();

    /**
     * Transform the input PersonModelView to one that only contains the necessary fields for avatar display.
     * 
     * @param inPeople
     *            list of the PersonModelViews to transform
     * @return a new list of PersonModelViews with the avatar fields
     */
    @Override
    public ArrayList<PersonModelView> transform(final List<PersonModelView> inPeople)
    {
        ArrayList<PersonModelView> returnPeople = new ArrayList<PersonModelView>();
        log.debug("Trimming " + inPeople.size()
                + " PersonModelViews of all information not necessary for avatar display");
        for (PersonModelView mv : inPeople)
        {
            PersonModelView newPerson = new PersonModelView();
            newPerson.setAccountId(mv.getAccountId());
            newPerson.setAvatarId(mv.getAvatarId());
            newPerson.setEntityId(mv.getEntityId());
            newPerson.setDisplayName(mv.getDisplayName());
            newPerson.setAccountLocked(mv.isAccountLocked());
            returnPeople.add(newPerson);
        }
        return returnPeople;
    }
}
