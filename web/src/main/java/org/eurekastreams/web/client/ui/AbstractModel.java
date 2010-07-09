/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An abstract base for models.
 */
public abstract class AbstractModel implements Model
{
    /**
     * Lists of events and associated listeners.
     */
    private HashMap<Object, ArrayList<ModelChangeListener>> changeListeners = 
        new HashMap<Object, ArrayList<ModelChangeListener>>();

    /**
     * Adds a change handler interested in an event.
     * 
     * @param event
     *            the relevant event.
     * @param handler
     *            the handler for the event.
     */
    public void addChangeListener(final Object event, final ModelChangeListener handler)
    {
        ArrayList<ModelChangeListener> handlers = changeListeners.get(event);

        if (null == handlers)
        {
            handlers = new ArrayList<ModelChangeListener>();
            changeListeners.put(event, handlers);
        }

        handlers.add(handler);
    }

    /**
     * Notifies the change handlers listeners to an event.
     * 
     * @param event
     *            the event.
     */
    protected void notifyChangeListeners(final Object event)
    {
        if (changeListeners.containsKey(event))
        {
            for (ModelChangeListener handler : changeListeners.get(event))
            {
                handler.onChange();
            }
        }
    }
}
