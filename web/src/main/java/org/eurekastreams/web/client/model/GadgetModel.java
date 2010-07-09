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

import org.eurekastreams.server.action.request.start.AddGadgetRequest;
import org.eurekastreams.server.action.request.start.ReorderGadgetRequest;
import org.eurekastreams.server.action.request.start.SetGadgetStateRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.web.client.events.GadgetAddedToStartPageEvent;
import org.eurekastreams.web.client.events.data.DeletedGadgetResponseEvent;
import org.eurekastreams.web.client.events.data.ReorderedGadgetResponseEvent;
import org.eurekastreams.web.client.events.data.UnDeletedGadgetResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedGadgetMinimizedStateResponseEvent;
import org.eurekastreams.web.client.model.requests.AddGadgetToStartPageRequest;
import org.eurekastreams.web.client.ui.Session;

/**
 * Represents a gadget on someone start page. NOT a gadget definition (see GadgetDefinitionModel for that).
 *
 */
public class GadgetModel extends BaseModel implements
    Deletable<Long>,
    Insertable<AddGadgetToStartPageRequest>,
    Reorderable<ReorderGadgetRequest>
{


    /**
     * Singleton.
     */
    private static GadgetModel model = new GadgetModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static GadgetModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deleteGadget", request, new OnSuccessCommand<Tab>()
        {
            public void onSuccess(final Tab response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeletedGadgetResponseEvent(request));
            }
        });

        StartTabsModel.getInstance().clearCache();
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final AddGadgetToStartPageRequest inRequest)
    {
        super.callWriteAction("addGadget", new AddGadgetRequest(inRequest.getTabId(), inRequest.getUrl()),
                new OnSuccessCommand<Gadget>()
                {
                    public void onSuccess(final Gadget response)
                    {
                        if (inRequest.getPrefs() != null)
                        {
                            response.setGadgetUserPref(inRequest.getPrefs());
                        }
                        Session.getInstance().getEventBus().notifyObservers(new GadgetAddedToStartPageEvent(response));
                    }
                });

        StartTabsModel.getInstance().clearCache();
    }

    /**
     * {@inheritDoc}
     */
    public void undoDelete(final Long request)
    {
        super.callWriteAction("undeleteGadget", request, new OnSuccessCommand<Gadget>()
        {
            public void onSuccess(final Gadget response)
            {
                Session.getInstance().getEventBus().notifyObservers(new UnDeletedGadgetResponseEvent(response));
            }
        });

        StartTabsModel.getInstance().clearCache();
    }

    /**
     * {@inheritDoc}
     */
    public void setState(final SetGadgetStateRequest request)
    {
        super.callWriteAction("setGadgetState", request, new OnSuccessCommand<Gadget>()
        {
            public void onSuccess(final Gadget response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new UpdatedGadgetMinimizedStateResponseEvent(response));

            }
        });

        StartTabsModel.getInstance().clearCache();
    }

    /**
     * {@inheritDoc}
     */
    public void reorder(final ReorderGadgetRequest request)
    {
        super.callWriteAction("reorderGadget", request, null);

        // We are making the call immediately because the drag and drop library has *ALREADY* moved the gadget
        // So things that want to know it's moved shouldn't have a delay or it can cause odd timing and
        // flashing issues.
        Session.getInstance().getEventBus().notifyObservers(new ReorderedGadgetResponseEvent());
        StartTabsModel.getInstance().clearCache();
    }

}
