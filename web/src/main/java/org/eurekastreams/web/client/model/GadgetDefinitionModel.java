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

import org.eurekastreams.server.action.request.gallery.GetGalleryItemsRequest;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.web.client.events.data.DeletedGadgetDefinitionResponseEvent;
import org.eurekastreams.web.client.events.data.GotGadgetDefinitionsResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedGadgetDefinitionResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedGadgetDefinitionResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Gadget definition model.
 *
 */
public class GadgetDefinitionModel extends BaseModel implements Insertable<HashMap<String, Serializable>>,
        Updateable<HashMap<String, Serializable>>, Deletable<Long>, Fetchable<GetGalleryItemsRequest>
{
    /**
     * Singleton.
     */
    private static GadgetDefinitionModel model = new GadgetDefinitionModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static GadgetDefinitionModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("addGadgetDefinition", request, new OnSuccessCommand<GadgetDefinition>()
        {
            public void onSuccess(final GadgetDefinition response)
            {
                Session.getInstance().getEventBus()
                        .notifyObservers(new InsertedGadgetDefinitionResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deleteGadgetDefinitionAction", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeletedGadgetDefinitionResponseEvent(request));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("editGadgetDefinition", request, new OnSuccessCommand<GadgetDefinition>()
        {
            public void onSuccess(final GadgetDefinition response)
            {
                Session.getInstance().getEventBus().notifyObservers(new UpdatedGadgetDefinitionResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final GetGalleryItemsRequest request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getSortedGadgetGalleryItems", request, new OnSuccessCommand<PagedSet<GadgetDefinition>>()
        {
            public void onSuccess(final PagedSet<GadgetDefinition> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotGadgetDefinitionsResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

}
