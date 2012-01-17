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
package org.eurekastreams.web.client.ui.connect.support;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;

/**
 * Widget which displays a feed icon linked to the feed for the current stream.
 */
public class StreamFeedLinkWidget extends Composite
{
    /**
     * Constructor.
     */
    public StreamFeedLinkWidget()
    {
        final Anchor atomLink = new Anchor();
        atomLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().streamAtomLink());
        atomLink.setTarget("_blank");

        initWidget(atomLink);

        EventBus.getInstance().addObserver(StreamRequestEvent.class, new Observer<StreamRequestEvent>()
        {
            public void update(final StreamRequestEvent event)
            {
                atomLink.setVisible(Session.getInstance().getParameterValue("search") == null
                        || Session.getInstance().getParameterValue("search").isEmpty());

                if (event.getStreamId() != null)
                {
                    atomLink.setHref("/resources/atom/stream/saved/" + event.getStreamId());
                }
                else
                {
                    JSONObject query = JSONParser.parse(event.getJson()).isObject().get("query").isObject();

                    if (query.containsKey("organization"))
                    {
                        atomLink.setHref("/resources/atom/stream/query/organization/"
                                + query.get("organization").isString().stringValue());
                    }
                    else if (query.containsKey("recipient"))
                    {
                        JSONArray recipients = query.get("recipient").isArray();
                        StringBuilder recipientLink = new StringBuilder();
                        recipientLink.append("/resources/atom/stream/query/recipient/");

                        for (int i = 0; i < recipients.size(); i++)
                        {
                            if (i > 0)
                            {
                                recipientLink.append(",");
                            }
                            JSONObject entityObject = recipients.get(i).isObject();
                            recipientLink.append(entityObject.get("type").isString().stringValue());
                            recipientLink.append(":");
                            recipientLink.append(entityObject.get("name").isString().stringValue());

                        }

                        atomLink.setHref(recipientLink.toString());
                    }
                }
            }
        });
    }
}
