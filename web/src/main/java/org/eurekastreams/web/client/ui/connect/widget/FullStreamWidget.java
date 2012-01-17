/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.connect.widget;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.connect.support.StreamPanel;
import org.eurekastreams.web.client.ui.connect.support.WidgetUtilities;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;

/**
 * Widget displaying a full interactive stream.
 */
public class FullStreamWidget extends Composite
{
    /**
     * Constructor.
     *
     * @param jsonRequest
     *            Stream query as a JSON string.
     */
    public FullStreamWidget(final String jsonRequest)
    {
        final StreamPanel streamPanel = new StreamPanel(ShowRecipient.YES,
                new FullStreamWidgetStreamMessageItemRenderer());
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().embeddedWidget());
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectFullStreamWidget());
        initWidget(streamPanel);

        EventBus.getInstance().notifyObservers(new StreamRequestEvent("", jsonRequest));
        StreamScope postingScope = getPostingScope(jsonRequest);
        if (postingScope != null)
        {
            streamPanel.setStreamScope(postingScope, true);
        }
    }

    /**
     * Determines the scope that the post box should post to.
     *
     * @param jsonRequestString
     *            The JSON activity query request.
     * @return The scope.
     */
    private StreamScope getPostingScope(final String jsonRequestString)
    {
        try
        {
            JSONObject request = StreamJsonRequestFactory.getJSONRequest(jsonRequestString);
            JSONObject query = request.get("query").isObject();
            if (!query.containsKey("keywords"))
            {
                JSONValue value = query.get("recipient");
                if (value != null)
                {
                    JSONArray recipients = value.isArray();
                    if (recipients.size() == 1)
                    {
                        JSONObject obj = recipients.get(0).isObject();
                        String type = obj.get("type").isString().stringValue();
                        String name = obj.get("name").isString().stringValue();
                        ScopeType scopeType = ScopeType.valueOf(type);
                        return new StreamScope(scopeType, name);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            int makeCheckstyleShutUp = 1;
        }
        return null;
    }

    /**
     * Custom version of the stream item renderer tailored to change the share behavior.
     */
    class FullStreamWidgetStreamMessageItemRenderer extends StreamMessageItemRenderer
    {
        /**
         * Constructor.
         */
        public FullStreamWidgetStreamMessageItemRenderer()
        {
            super(ShowRecipient.YES);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onShare(final ActivityDTO activity)
        {
            // display share box in a new pop-up window
            WidgetUtilities.showShareActivityPopup(activity.getId());
        }
    }
}
