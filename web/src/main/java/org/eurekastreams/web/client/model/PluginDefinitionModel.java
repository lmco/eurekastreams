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
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.web.client.events.data.DeletedPluginDefinitionResponseEvent;
import org.eurekastreams.web.client.events.data.GotPluginDefinitionModelResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedPluginDefinitionResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Plugin definition model.
 *
 */
public class PluginDefinitionModel extends BaseModel implements Insertable<HashMap<String, Serializable>>,
        Deletable<Long>, Fetchable<GetGalleryItemsRequest>
{
    /**
     * Singleton.
     */
    private static PluginDefinitionModel model = new PluginDefinitionModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static PluginDefinitionModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("addPluginDefinition", request, new OnSuccessCommand<PluginDefinition>()
        {
            public void onSuccess(final PluginDefinition response)
            {
                Session.getInstance().getEventBus()
                        .notifyObservers(new InsertedPluginDefinitionResponseEvent(response));

                PersonStreamPluginSubscriptionModel.getInstance().clearCache();
                GroupStreamPluginSubscriptionModel.getInstance().clearCache();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deletePluginDefinitionAction", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeletedPluginDefinitionResponseEvent(request));

                PersonStreamPluginSubscriptionModel.getInstance().clearCache();
                GroupStreamPluginSubscriptionModel.getInstance().clearCache();
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    public void fetch(final GetGalleryItemsRequest request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getSortedPluginGalleryItems", request, new OnSuccessCommand<PagedSet<PluginDefinition>>()
        {
            public void onSuccess(final PagedSet<PluginDefinition> response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new GotPluginDefinitionModelResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

}
