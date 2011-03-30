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
package org.eurekastreams.web.client.ui.common.stream;

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays the title of a stream as text or a link.
 */
public class StreamTitleWidget extends Composite
{
    /** The title label. */
    private final Label titleLbl = new InlineLabel();

    /** Link for stream titles. */
    Hyperlink titleLink = new InlineHyperlink();

    /**
     * Constructor.
     */
    public StreamTitleWidget()
    {
        FlowPanel mainPanel = new FlowPanel();
        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().titleWrapper());

        InlineLabel fader = new InlineLabel();
        fader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().streamTitleFader());
        mainPanel.add(fader);

        titleLbl.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        mainPanel.add(titleLbl);

        titleLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        titleLink.setVisible(false);
        mainPanel.add(titleLink);

        initWidget(mainPanel);
    }

    /**
     * Sets the stream title.
     *
     * @param title
     *            The stream title.
     * @param url
     *            URL to link title to.
     */
    public void setStreamTitle(final String title, final String url)
    {
        if (url == null)
        {
            titleLbl.setText(title);
            titleLbl.setVisible(true);
            titleLink.setVisible(false);
        }
        else
        {
            titleLink.setTargetHistoryToken(url);
            titleLink.setText(title);
            titleLink.setVisible(true);
            titleLbl.setVisible(false);
        }
    }

}
