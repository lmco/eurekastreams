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

import org.eurekastreams.server.action.request.start.RenameTabRequest;
import org.eurekastreams.server.action.request.start.SetTabLayoutRequest;
import org.eurekastreams.server.action.request.start.SetTabOrderRequest;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdateGadgetPrefsEvent;
import org.eurekastreams.web.client.events.data.DeletedStartPageTabResponseEvent;
import org.eurekastreams.web.client.events.data.GotStartPageTabsResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedStartTabResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedStartPageLayoutResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedStartPageTabNameResponseEvent;
import org.eurekastreams.web.client.model.requests.PersonPagePropertiesDTOToPersonTransformer;
import org.eurekastreams.web.client.ui.Session;

/**
 * Represents the tabs on someones start page. Allows you to get them, change their layout, rename them, reorder them,
 * add them and delete them,
 *
 */
public final class StartTabsModel extends BaseModel implements Fetchable<String>, Insertable<String>, Deletable<Tab>,
        Reorderable<SetTabOrderRequest>
{
    /**
     * Singleton.
     */
    private static StartTabsModel model = new StartTabsModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static StartTabsModel getInstance()
    {
        return model;
    }

    /**
     * Constructor.
     */
    private StartTabsModel()
    {
        /*
         * When gadget preferences are updated (via UpdateGadgetPrefsEvent), GadgetPanel updates its copy of the gadget
         * data. However, GadgetPanel holds a separate copy, not just a reference, so the copy stored here in the cache
         * is out of date. (GadgetPanel holds a Gadget whereas the cache here holds a GadgetDTO.) We flush the cache to
         * prevent returning old data. If the rest of the code was updated to use the DTOs instead of the entities (see
         * PersonPagePropertiesDTOToPersonTransformer) then the update done by GadgetPanel would update the cached copy,
         * and we wouldn't need to flush the cache and incur the associated performance penalty.
         */
        Session.getInstance().getEventBus()
                .addObserver(UpdateGadgetPrefsEvent.class, new Observer<UpdateGadgetPrefsEvent>()
                {
                    public void update(final UpdateGadgetPrefsEvent inArg1)
                    {
                        clearCache();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final String request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getPersonPageProperties", request, new OnSuccessCommand<PersonPagePropertiesDTO>()
        {
            public void onSuccess(final PersonPagePropertiesDTO response)
            {
                PersonPagePropertiesDTOToPersonTransformer transformer = //
                new PersonPagePropertiesDTOToPersonTransformer();
                Session.getInstance().getEventBus().notifyObservers(
                        new GotStartPageTabsResponseEvent(transformer.transform(response)));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final String request)
    {
        super.callWriteAction("addTab", request, new OnSuccessCommand<Tab>()
        {
            public void onSuccess(final Tab response)
            {
                Session.getInstance().getEventBus().notifyObservers(new InsertedStartTabResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Tab request)
    {
        super.callWriteAction("deleteTab", request.getId(), new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeletedStartPageTabResponseEvent(request));
            }
        });
    }

    /**
     * Rename a tab.
     *
     * @param request
     *            the request.
     */
    public void rename(final RenameTabRequest request)
    {
        super.callWriteAction("renameTab", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new UpdatedStartPageTabNameResponseEvent(request.getTabId()));
            }
        });
    }

    /**
     * Undo the delete of a tab.
     *
     * @param request
     *            the id of the tab to undelete.
     */
    public void undoDelete(final Long request)
    {
        super.callWriteAction("undeleteTab", request, new OnSuccessCommand<Tab>()
        {
            public void onSuccess(final Tab response)
            {
                Session.getInstance().getEventBus().notifyObservers(new InsertedStartTabResponseEvent(response));
            }
        });
    }

    /**
     * Set the layout of the tab.
     *
     * @param request
     *            the request.
     */
    public void setLayout(final SetTabLayoutRequest request)
    {
        super.callWriteAction("setTabLayout", request, new OnSuccessCommand<Tab>()
        {
            public void onSuccess(final Tab response)
            {
                Session.getInstance().getEventBus().notifyObservers(new UpdatedStartPageLayoutResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void reorder(final SetTabOrderRequest request)
    {
        super.callWriteAction("setTabOrder", request, null);
    }
}
