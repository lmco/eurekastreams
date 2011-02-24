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

import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.events.data.AuthorizeUpdateGroupResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedGroupResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedGroupResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Group model.
 * 
 */
public class GroupModel extends BaseModel implements Authorizable<String>, Fetchable<String>,
        Insertable<HashMap<String, Serializable>>, Updateable<HashMap<String, Serializable>>
{
    /**
     * Singleton.
     */
    private static GroupModel model = new GroupModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static GroupModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void authorize(final String request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("authorizeUpdateGroup", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(new AuthorizeUpdateGroupResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("createGroup", request, new OnSuccessCommand<DomainGroupModelView>()
        {
            public void onSuccess(final DomainGroupModelView response)
            {
                Session.getInstance().getEventBus().notifyObservers(new InsertedGroupResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final String request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getGroupModelView", request, new OnSuccessCommand<DomainGroupModelView>()
        {
            public void onSuccess(final DomainGroupModelView response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new GotGroupModelViewInformationResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("updateGroup", request, new OnSuccessCommand<DomainGroupModelView>()
        {
            public void onSuccess(final DomainGroupModelView response)
            {
                Session.getInstance().getEventBus().notifyObservers(new UpdatedGroupResponseEvent(response));
            }
        });
    }

}
