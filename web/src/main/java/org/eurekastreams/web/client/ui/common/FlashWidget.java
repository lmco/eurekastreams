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
package org.eurekastreams.web.client.ui.common;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * Widget to embed a flash object.
 * 
 */
public class FlashWidget extends FlowPanel
{
    /**
     * The html for the embedded object.
     */
    HTML videoEmbedHtml = new HTML();

    /**
     * @param videoURL
     *            url to flash object.
     * @param key
     *            the name to set for the object.
     * @param width
     *            the optional width.
     * @param height
     *            the optional height.
     */
    public FlashWidget(final String videoURL, final String key, final Integer width, final Integer height)
    {
        setFlashWidget(videoURL, key, width, height);
    }

    /**
     * Default constructor.
     */
    public FlashWidget()
    {
        super();
    }

    /**
     * @param inVideoURL
     *            url to flash object.
     * @param inKey
     *            the name to set for the object.
     * @param width
     *            the optional width.
     * @param height
     *            the optional height.
     */
    public void setFlashWidget(final String inVideoURL, final String inKey, final Integer width, final Integer height)
    {
        String widthHTML = "";
        String heightHTML = "";

        if (width != null)
        {
            widthHTML = "width='" + width + "'";
        }

        if (height != null)
        {
            heightHTML = "height='" + height + "'";
        }

        String videoURL = URL.encode(inVideoURL);

        HTML key = new HTML();
        key.setText(inKey);

        videoEmbedHtml.setHTML("<object " + widthHTML + " " + heightHTML + " >" + "<param name='" + key.getText()
                + "' value='" + videoURL + "'></param>" + "<param name='allowFullScreen' value='true'></param>"
                + "<embed src='" + videoURL + "' allowFullScreen='true'" + widthHTML + " " + heightHTML + " >"
                + "</embed>" + "</object>");

        this.add(videoEmbedHtml);
    }
}
