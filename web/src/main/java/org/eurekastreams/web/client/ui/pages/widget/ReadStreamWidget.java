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
package org.eurekastreams.web.client.ui.pages.widget;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.ui.common.stream.StreamPanel;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;

/**
 * Widget displaying a basic read-only stream.
 */
public class ReadStreamWidget extends Composite
{
    /**
     * Constructor.
     *
     * @param jsonRequest
     *            Stream query as a JSON string.
     */
    public ReadStreamWidget(final String jsonRequest)
    {
        final StreamPanel streamPanel = new StreamPanel(ShowRecipient.ALL);
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().embeddedWidget());
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectReadStreamWidget());
        initWidget(streamPanel);

        EventBus.getInstance().notifyObservers(new StreamRequestEvent("", jsonRequest));
    }
}
