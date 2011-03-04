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
package org.eurekastreams.web.client.ui.common.stream.renderers.object;

import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders a file (document) object.
 *
 */
public class FileRenderer implements ObjectRenderer
{
    /**
     * {@inheritDoc}
     */
    public Widget getAttachmentWidget(final ActivityDTO activity)
    {
        HashMap<String, String> props = activity.getBaseObjectProperties();
        String title = props.get("targetTitle");
        String url = props.get("targetUrl");
        String source = props.get("source");
        String fileExt = getFileExt(url);

        FlowPanel mainPanel = new FlowPanel();
        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().icon());
        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageLink());
        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hasThumbnail());
        if (fileExt != null)
        {
            mainPanel.addStyleName("icon-" + fileExt);
        }

        FlowPanel line;
        Label text;
        Widget link;

        link = new Anchor(title, url);
        link.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        mainPanel.add(link);

        // source line
        line = new FlowPanel();
        line.addStyleName(StaticResourceBundle.INSTANCE.coreCss().url());
        text = new InlineLabel("source: ");
        line.add(text);
        link = new Anchor(source, source);
        line.add(link);
        mainPanel.add(line);

        // "modified by" line
        String authorName = props.get("modifiedByDisplayName");
        if (authorName != null)
        {
            String authorAccountId = props.get("modifiedByAccountId");
            if (authorAccountId != null)
            {
                line = new FlowPanel();
                line.addStyleName(StaticResourceBundle.INSTANCE.coreCss().url());
                text = new InlineLabel("Modified by: ");
                line.add(text);
                String authorUrl =
                        Session.getInstance().generateUrl(new CreateUrlRequest(Page.PEOPLE, authorAccountId));
                link = new InlineHyperlink(authorName, authorUrl);
                line.add(link);
                mainPanel.add(line);
            }
            else
            {
                text = new Label("Modified by:  " + authorName);
                text.addStyleName(StaticResourceBundle.INSTANCE.coreCss().url());
                mainPanel.add(text);
            }
        }

        return mainPanel;
    }

    /**
     * {@inheritDoc}
     */
    public Widget getContentWidget(final ActivityDTO activity)
    {
        // no content other than the attachment
        return null;
    }

    /**
     * Gets the file extension from a URL. (Native since GWT 1.7 doesn't support java.util.regex.)
     *
     * @param url
     *            the url.
     * @return File extension.
     */
    private static native String getFileExt(final String url)
    /*-{
        var re = new RegExp('\\.(\\w+)$');
        var result = url.match(re);
        return result == null ? null : result[1].toString();
    }-*/;

}
