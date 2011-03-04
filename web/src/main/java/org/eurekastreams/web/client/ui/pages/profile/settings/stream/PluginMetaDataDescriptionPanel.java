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
package org.eurekastreams.web.client.ui.pages.profile.settings.stream;

import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Panel.
 *
 */
public class PluginMetaDataDescriptionPanel extends FlowPanel
{
    /**
     * Constructor.
     * @param metaData metadata.
     */
    public PluginMetaDataDescriptionPanel(final GadgetMetaDataDTO metaData)
    {
        FlowPanel feedContainer = new FlowPanel();
        feedContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().streamPluginsAddFeedMetaData());

        FlowPanel imageContainer = new FlowPanel();
        imageContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().imageContainer());

        FlowPanel screenshot = new FlowPanel();
        screenshot.addStyleName(StaticResourceBundle.INSTANCE.coreCss().streamPluginsScreenshot());

        screenshot.add(new Image(
                metaData.getScreenshot()));

        imageContainer.add(screenshot);

        FlowPanel dataContainer = new FlowPanel();
        dataContainer.setStyleName(StaticResourceBundle.INSTANCE.coreCss().gadgetData());

        Label title = new Label(metaData.getTitle());
        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());

        String[] descriptionArray = metaData.getDescription().split("\\|");
        HTML description = new HTML(convertToSafeHTML(descriptionArray[0]));
        description.addStyleName(StaticResourceBundle.INSTANCE.coreCss().description());

        dataContainer.add(title);
        dataContainer.add(description);

        feedContainer.add(imageContainer);
        feedContainer.add(dataContainer);

        this.add(feedContainer);
    }

    /**
     * Converts &lt; to <, &quot; to " and &gt; to > for whitelisted HTML elements only.
     * Currently supports:
     *      <ol></ol>
     *      <li>
     *      <a href="something"></a>
     *      "
     *
     * @param inString
     *            The string to convert.
     * @return
     *            The string with html elements
     */
    private String convertToSafeHTML(final String inString)
    {
        String convertedString = inString;

        convertedString = convertedString.replace("&lt;ol&gt;", "<ol>");
        convertedString = convertedString.replace("&lt;/ol&gt;", "</ol>");
        convertedString = convertedString.replace("&lt;li&gt;", "<li>");
        convertedString = convertedString.replace("&lt;a", "<a");
        convertedString = convertedString.replace("&quot;&gt;", "\">");
        convertedString = convertedString.replace("&lt;/a&gt;", "</a>");
        convertedString = convertedString.replace("&quot;", "\"");

        return convertedString;
    }
}
