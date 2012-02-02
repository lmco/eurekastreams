/*
 * Copyright (c) 2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.renderers.content;


/**
 * Splits content (activity/comment bodies) into text and link segments based on several filters including hashtags,
 * markdown, and URLs.
 */
public class ContentParser
{
    /**
     * Splits a string of content into segments. The content of the segments is not escaped.
     *
     * @param text
     *            The text.
     * @return The first segment in a linked list of segments.
     */
    public native ContentSegment split(final String text)
    /*-{
         var ns = $wnd.EurekaStreams.Client;
         return ns.textToList(text, [ns.markdownLinkFilter, ns.plainUrlFilter, ns.hashtagFilter, ns.newlineFilter]);
     }-*/;
}
