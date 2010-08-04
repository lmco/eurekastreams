/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.master;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Controller for the master composite.
 */
public class MasterCompositeController implements Bindable
{
    /**
     * The header panel.
     */
    FlowPanel headerPanel;

    /**
     * The assocaited view.
     */
    private MasterComposite view;

    /**
     * The action processor.
     */
    private ActionProcessor processor;

    /**
     * Call native JS methods.
     */
    private WidgetJSNIFacade jsniFacade;

    /**
     * The session.
     */
    private Session session = null;

    /**
     * The event bus.
     */
    private EventBus eventBus = null;

    /**
     * Constructor.
     *
     * @param inView
     *            the view.
     * @param inProcessor
     *            the action processor.
     * @param inJsniFacade
     *            the JSNI facade.
     * @param inSession
     *            the session.
     * @param inEventBus
     *            the event bus.
     */
    public MasterCompositeController(final MasterComposite inView, final ActionProcessor inProcessor,
            final WidgetJSNIFacade inJsniFacade, final Session inSession, final EventBus inEventBus)
    {
        view = inView;
        processor = inProcessor;
        jsniFacade = inJsniFacade;
        session = inSession;
        eventBus = inEventBus;
    }
}
