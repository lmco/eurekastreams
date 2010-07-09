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

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.web.client.events.StreamSearchCreatedEvent;
import org.eurekastreams.web.client.events.StreamSearchDeletedEvent;
import org.eurekastreams.web.client.events.StreamSearchUpdatedEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Stream Search Model.
 * 
 */
public class StreamSearchModel extends BaseModel implements Insertable<HashMap<String, Serializable>>,
        Updateable<HashMap<String, Serializable>>, Deletable<StreamSearch>
{
    /**
     * Singleton.
     */
    private static StreamSearchModel model = new StreamSearchModel();

    /**
     * Name of the view associated with the search. Needed to modify the response object.
     */
    private static String viewName = null;

    /**
     * Gets the singleton.
     * 
     * @param inViewName
     *            the view name.
     * @return the singleton.
     */
    public static StreamSearchModel getInstance(final String inViewName)
    {
        viewName = inViewName;
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("createStreamSearch", request, new OnSuccessCommand<StreamSearch>()
        {
            public void onSuccess(final StreamSearch response)
            {
                StreamSearchListModel.getInstance().clearCache();
                parentOrgViewNameChanger(response);

                Session.getInstance().getEventBus().notifyObservers(new StreamSearchCreatedEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("updateStreamSearch", request, new OnSuccessCommand<StreamSearch>()
        {
            public void onSuccess(final StreamSearch response)
            {
                StreamSearchListModel.getInstance().clearCache();
                parentOrgViewNameChanger(response);
                Session.getInstance().getEventBus().notifyObservers(new StreamSearchUpdatedEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final StreamSearch request)
    {
        super.callWriteAction("deleteStreamSearch", request.getId(), new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                StreamSearchListModel.getInstance().clearCache();
                Session.getInstance().getEventBus().notifyObservers(new StreamSearchDeletedEvent(request));
            }
        });
    }

    /**
     * This method examines the search's stream view and replaces the generic parent org token.
     * 
     * @param inStreamSearch
     *            StreamSearch to check.
     */
    private void parentOrgViewNameChanger(final StreamSearch inStreamSearch)
    {
        if (inStreamSearch.getStreamView().getName().compareToIgnoreCase("EUREKA:PARENT_ORG_TAG") == 0)
        {
            inStreamSearch.getStreamView().setName(
                    Session.getInstance().getCurrentPerson().getParentOrganization().getName());
        }
    }
}
