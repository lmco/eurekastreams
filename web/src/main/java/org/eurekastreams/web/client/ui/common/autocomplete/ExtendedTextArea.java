/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.autocomplete;

import org.eurekastreams.web.client.ui.common.LabeledTextArea;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.Event;

/**
 * Subclass of TextArea that handles onPaste events, registering them as onChanged.
 */
public class ExtendedTextArea extends LabeledTextArea
{

    /**
     * If the box is auto resizing.
     */
    private boolean resizing;

    /**
     * Constructor.
     * 
     * @param autoresize
     *            if the box should autoresize.
     */
    @UiConstructor
    public ExtendedTextArea(final boolean autoresize)
    {
        super("");
        resizing = autoresize;

        if (resizing)
        {
            addStyleName(StaticResourceBundle.INSTANCE.coreCss().resizingTextArea());
        }

        sinkEvents(Event.ONPASTE);

        addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent event)
            {
                resize();
            }
        });

        addChangeHandler(new ChangeHandler()
        {
            public void onChange(final ChangeEvent event)
            {
                resize();
            }
        });

    }

    /**
     * Resize if necessary.
     */
    public void resize()
    {
        if (resizing && getElement().getClientHeight() < getElement().getScrollHeight())
        {
            getElement().getStyle().setHeight(getElement().getScrollHeight(), Unit.PX);
        }
    }

    /**
     * Override to get the browser events.
     * 
     * @param event
     *            the event that fired
     */
    @Override
    public void onBrowserEvent(final Event event)
    {
        super.onBrowserEvent(event);
        switch (event.getTypeInt())
        {
        case Event.ONPASTE:
            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {
                public void execute()
                {
                    ValueChangeEvent.fire(ExtendedTextArea.this, getText());
                }
            });
            break;
        default:
            break;
        }
    }
}
