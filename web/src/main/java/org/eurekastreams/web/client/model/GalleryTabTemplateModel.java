/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
import org.eurekastreams.server.domain.dto.GalleryTabTemplateDTO;
import org.eurekastreams.web.client.events.data.DeletedGalleryTabTemplateResponse;
import org.eurekastreams.web.client.events.data.GotGalleryTabTemplateDTOResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedGalleryTabTempalateResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedGalleryTabTemplateResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * GalleryTabTemplate model.
 * 
 */
public class GalleryTabTemplateModel extends BaseModel implements Insertable<HashMap<String, Serializable>>,
        Deletable<Long>, Fetchable<GetGalleryItemsRequest>, Updateable<HashMap<String, Serializable>>

{

    /**
     * Singleton.
     */
    private static GalleryTabTemplateModel model = new GalleryTabTemplateModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static GalleryTabTemplateModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> inRequest)
    {
        super.callWriteAction("createGalleryTabTemplate", inRequest, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new InsertedGalleryTabTempalateResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deleteGalleryTabTemplate", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeletedGalleryTabTemplateResponse(request));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final GetGalleryItemsRequest request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getSortedGalleryTabTemplateDTOs", request,
                new OnSuccessCommand<PagedSet<GalleryTabTemplateDTO>>()
                {
                    public void onSuccess(final PagedSet<GalleryTabTemplateDTO> response)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new GotGalleryTabTemplateDTOResponseEvent(response));
                    }
                }, useClientCacheIfAvailable);

    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("editGalleryTabTemplate", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new UpdatedGalleryTabTemplateResponseEvent(response));
            }
        });
    }

}
