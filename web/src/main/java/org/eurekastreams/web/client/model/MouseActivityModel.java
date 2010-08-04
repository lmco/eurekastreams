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

import org.eurekastreams.web.client.events.UserActiveEvent;
import org.eurekastreams.web.client.events.UserInactiveEvent;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

/**
 * Mouse movement activity model. Detects if the mouse position has changed since the last time fetch() was called.
 * Triggers an event to signal the active/inactive state.
 */
public class MouseActivityModel extends BaseModel implements Fetchable<Serializable>
{
    /**
     * Singleton.
     */
    private static MouseActivityModel model = new MouseActivityModel();

    /**
     * Last x coordinate of the mouse position.
     */
    private int lastX = 0;

    /**
     * Last y coordinate of the mouse position.
     */
    private int lastY = 0;

    /**
     * Current x coordinate of the mouse position.
     */
    private int currentX = 0;

    /**
     * Current y position of the mouse position.
     */
    private int currentY = 0;

    /**
     * Global loop iteration counter.
     */
    private int iteration = 0;

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static MouseActivityModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        iteration++;

        int iterationsUntilTimeout = (Integer) request;

        Event.addNativePreviewHandler(new NativePreviewHandler()
        {
            public void onPreviewNativeEvent(final NativePreviewEvent event)
            {
                if (event.getNativeEvent().getType().equals("mousemove"))
                {
                    currentX = event.getNativeEvent().getClientX();
                    currentY = event.getNativeEvent().getClientY();
                }
            }
        });

        boolean mouseHasMoved = (lastX != currentX || lastY != currentY);
        if (mouseHasMoved)
        {
            iteration = 0;
            Session.getInstance().getEventBus().notifyObservers(UserActiveEvent.getEvent());
        }
        else if (!mouseHasMoved && iteration >= iterationsUntilTimeout)
        {
            iteration = 0;
            Session.getInstance().getEventBus().notifyObservers(UserInactiveEvent.getEvent());
        }

        lastX = currentX;
        lastY = currentY;

    }
}
