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
package org.eurekastreams.web.client.ui.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * This Command acts as a composite, wrapping multiple ClickListeners. This
 * allows us to use the same listeners on MenuItems as we use on buttons. (A
 * MenuItem takes a single command, whereas a button takes multiple
 * ClickListeners.)
 */
public class ClickListenerCommand implements Command
{
    /**
     * The collection of listeners to apply to the Widget.
     */
    private List<ClickListener> listeners = new ArrayList<ClickListener>();

    /**
     * The Widget to apply the listeners to.
     */
    private Widget sender = null;

    /**
     * Constructor.
     * 
     * @param inWidget
     *            the widget to apply the commands to.
     */
    public ClickListenerCommand(final Widget inWidget)
    {
        sender = inWidget;
    }

    /**
     * Fire all the ClickListeners.
     */
    public void execute()
    {
        for (ClickListener listener : listeners)
        {
            listener.onClick(sender);
        }
    }

    /**
     * Add a listener.
     * 
     * @param listener
     *            the listener to add
     */
    public void addClickListener(final ClickListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a listener from the list.
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeClickListener(final ClickListener listener)
    {
        listeners.remove(listener);
    }
}
