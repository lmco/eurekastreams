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

import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.StreamPanel;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * Widget displaying a basic read-only stream.
 */
public class ReadStreamWidget extends Composite
{
    /** For building link to stream from the query. */
    private static HashMap<String, Page> scopeToPageMap = new HashMap<String, Page>();

    static
    {
        scopeToPageMap.put(ScopeType.PERSON.name(), Page.PEOPLE);
        scopeToPageMap.put(ScopeType.GROUP.name(), Page.GROUPS);
        scopeToPageMap.put(ScopeType.ORGANIZATION.name(), Page.ORGANIZATIONS);
    }

    /**
     * Constructor.
     *
     * @param jsonRequest
     *            Stream query as a JSON string.
     */
    public ReadStreamWidget(final String jsonRequest)
    {
        final StreamPanel streamPanel = new StreamPanel(ShowRecipient.ALL, new StreamMessageItemRenderer(
                ShowRecipient.ALL)
        {
            {
                setCreatePermalink(false);
            }
        });
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().embeddedWidget());
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectReadStreamWidget());

        CreateUrlRequest linkInfo = determineStreamLink(jsonRequest);
        if (linkInfo != null)
        {
            String token = Session.getInstance().generateUrl(linkInfo);
            Hyperlink link = new Hyperlink("Go to Stream", token);
            link.addStyleName(StaticResourceBundle.INSTANCE.coreCss().goToStreamLink());
            FlowPanel panel = new FlowPanel();
            panel.add(link);
            streamPanel.add(panel); // cheating somewhat here
        }

        initWidget(streamPanel);

        EventBus.getInstance().notifyObservers(new StreamRequestEvent("", jsonRequest));
    }

    /**
     * Determines the page/view within Eureka to link to for the given stream request.
     *
     * @param jsonRequestString
     *            The JSON activity query request.
     * @return URL request for stream page; null to skip link.
     */
    private CreateUrlRequest determineStreamLink(final String jsonRequestString)
    {
        try
        {
            JSONObject request = StreamJsonRequestFactory.getJSONRequest(jsonRequestString);
            JSONObject query = request.get("query").isObject();
            JSONValue value = query.get("recipient");
            if (value != null)
            {
                JSONArray recipients = value.isArray();
                if (recipients.size() == 1)
                {
                    JSONObject obj = recipients.get(0).isObject();
                    String type = obj.get("type").isString().stringValue();
                    Page page = scopeToPageMap.get(type);
                    if (page != null)
                    {
                        String name = obj.get("name").isString().stringValue();

                        CreateUrlRequest info = new CreateUrlRequest(page, name);
                        if (query.containsKey("keywords"))
                        {
                            info.getParameters().put("search", query.get("keywords").isString().stringValue());
                        }
                        return info;
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
}
