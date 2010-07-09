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
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.web.client.events.ThemeChangedEvent;
import org.eurekastreams.web.client.events.data.DeletedThemeResponseEvent;
import org.eurekastreams.web.client.events.data.GotThemeDefinitionsResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedThemeResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedThemeResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Theme Model.
 *
 */
public class ThemeModel extends BaseModel implements Insertable<HashMap<String, Serializable>>,
        Updateable<HashMap<String, Serializable>>, Deletable<Long>, Fetchable<GetGalleryItemsRequest>
{
    /**
     * Singleton.
     */
    private static ThemeModel model = new ThemeModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static ThemeModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("addTheme", request, new OnSuccessCommand<Theme>()
        {
            public void onSuccess(final Theme response)
            {
                Session.getInstance().getEventBus().notifyObservers(new InsertedThemeResponseEvent(response));
            }
        });
    }

    /**
     * Set the theme for the current user.
     *
     * @param request
     *            the theme.
     */
    public void set(final Theme request)
    {
        super.callWriteAction("setPersonTheme", "{" + request.getUUID() + "}", new OnSuccessCommand<String>()
        {
            public void onSuccess(final String response)
            {
                Session.getInstance().getEventBus().notifyObservers(new ThemeChangedEvent(request));
            }
        });

        StartTabsModel.getInstance().clearCache();
    }

    /**
     * Set the theme for the current user.
     *
     * @param themeUrlOrUUID
     *            the theme url or UUID.
     */
    public void set(final String themeUrlOrUUID)
    {
        super.callWriteAction("setPersonTheme", themeUrlOrUUID, new OnSuccessCommand<String>()
        {
            public void onSuccess(final String response)
            {
                Session.getInstance().getEventBus().notifyObservers(new ThemeChangedEvent(null));
            }
        });

        StartTabsModel.getInstance().clearCache();
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deleteThemeAction", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeletedThemeResponseEvent(request));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("editTheme", request, new OnSuccessCommand<Theme>()
        {
            public void onSuccess(final Theme response)
            {
                Session.getInstance().getEventBus().notifyObservers(new UpdatedThemeResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final GetGalleryItemsRequest request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getSortedThemeGalleryItems", request, new OnSuccessCommand<PagedSet<Theme>>()
        {
            public void onSuccess(final PagedSet<Theme> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotThemeDefinitionsResponseEvent(response));
            }
        }, useClientCacheIfAvailable);

    }

}
