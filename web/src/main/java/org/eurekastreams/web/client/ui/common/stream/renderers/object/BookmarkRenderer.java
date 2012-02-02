/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.renderers.object;

import java.util.HashMap;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders a bookmark object.
 *
 */
public class BookmarkRenderer extends ActivityWithContentRenderer
{
    /**
     * Renders the bookmark attachment.
     *
     * @param activity
     *            the activity.
     * @return the widget.
     */
    @Override
    public Widget getAttachmentWidget(final ActivityDTO activity)
    {
        HashMap<String, String> props = activity.getBaseObjectProperties();
        String thumbnailUrl = props.get("thumbnail");
        String targetUrl = props.get("targetUrl");
        String sourceUrl = props.get("source");
        if (sourceUrl == null)
        {
            sourceUrl = getBaseUrl(targetUrl);
        }

        FlowPanel main = new FlowPanel();
        main.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageLink());
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty())
        {
            main.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hasThumbnail());

            Image image = new Image(thumbnailUrl);
            image.addStyleName(StaticResourceBundle.INSTANCE.coreCss().thumbnail());
            main.add(image);
        }

        Anchor primaryLink = new Anchor(props.get("targetTitle"), props.get("targetUrl"), "_blank");
        primaryLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        main.add(primaryLink);

        FlowPanel sourcePanel = new FlowPanel();
        sourcePanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().url());
        sourcePanel.add(new InlineLabel("source: "));
        sourcePanel.add(new Anchor(sourceUrl, sourceUrl, "_blank"));
        main.add(sourcePanel);

        Label meta = new Label(props.get("description"));
        meta.addStyleName(StaticResourceBundle.INSTANCE.coreCss().metaDescription());
        main.add(meta);

        return main;
    }

    /**
     * Gets the base URL (host name and protocol) from a url.
     *
     * @param url
     *            the url.
     * @return the base url.
     */
    private static native String getBaseUrl(final String url)
    /*-{
        try {
            var re = new RegExp('^([a-z]+://[^/]+)', 'im');
            return url.match(re)[1].toString();
        } catch(e)
        {
            return url;
        }
    }-*/;
}
