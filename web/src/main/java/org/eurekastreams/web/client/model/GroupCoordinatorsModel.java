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
package org.eurekastreams.web.client.model;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.data.GotGroupCoordinatorsResponseEvent;
import org.eurekastreams.web.client.model.requests.GetGroupCoordinatorsRequest;
import org.eurekastreams.web.client.ui.Session;

/**
 * Group Coordinators Model.
 * 
 */
public class GroupCoordinatorsModel implements Fetchable<GetGroupCoordinatorsRequest>
{
    /**
     * Singleton.
     */
    private static GroupCoordinatorsModel model = new GroupCoordinatorsModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static GroupCoordinatorsModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final GetGroupCoordinatorsRequest request, final boolean useClientCacheIfAvailable)
    {

        int count = 0;

        List<PersonModelView> peopleList = new ArrayList<PersonModelView>();
        for (PersonModelView personMv : request.getCoordinators())
        {
            if (count >= request.getStartIndex() && count <= request.getEndIndex())
            {
                peopleList.add(personMv);
            }
            count++;
        }

        PagedSet<PersonModelView> people = new PagedSet<PersonModelView>();
        people.setTotal(request.getCoordinators().size());
        people.setPagedSet(peopleList);
        Session.getInstance().getEventBus().notifyObservers(new GotGroupCoordinatorsResponseEvent(people));
    }
}
