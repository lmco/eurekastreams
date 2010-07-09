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
package org.eurekastreams.web.client.ui.pages.profile.widgets;

import java.util.List;

import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;

/**
 * Panel which displays a set of links for background items.
 */
public class BackgroundItemLinksPanel extends FlowPanel
{
    /**
     * Constructor.
     *
     * @param contentDescription
     *            Words to describe the type of content (used in "none" messages).
     * @param items
     *            List of items.
     */
    public BackgroundItemLinksPanel(final String contentDescription, final List<BackgroundItem> items)
    {
        addStyleName("profile-about-background-item-links");

        if (items != null && !items.isEmpty())
        {
            Element element = getElement();

            boolean first = true;
            for (BackgroundItem item : items)
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    element.appendChild(element.getOwnerDocument().createTextNode(", "));
                }

                String text = item.getName();
                String url = Session.getInstance().generateUrl(new CreateUrlRequest(Page.SEARCH, "query", text));
                InlineHyperlink link = new InlineHyperlink(text, url);
                link.addStyleName("profile-about-backgrounditem-link");
                add(link);
            }
        }
    }
}
