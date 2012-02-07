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

import org.eurekastreams.web.client.ui.common.stream.transformers.StreamSearchLinkBuilder;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;

/**
 * Renders split content (per ContentParser) into HTML DOM.
 */
public class ParsedContentRenderer
{
    /**
     * Renders a single segment into the supplied container element (appending to existing content).
     *
     * @param segment
     *            The segment.
     * @param parent
     *            The container element.
     * @param streamSearchLinkBuilder
     *            For building links for hashtags.
     */
    public void renderSegment(final ContentSegment segment, final ComplexPanel parent,
            final StreamSearchLinkBuilder streamSearchLinkBuilder)
    {
        // Notes:
        // * The element and widget setters automatically HTML encode content, hence it is not explicitly done.
        // * Using plain elements instead of widgets to keep markup cleaner, except for internal links. Although the
        // internal links will go to the right place when implemented as plain anchors, IE will lose all its history.
        // (So really this is working around another IE bug.)

        final Element parentElement = parent.getElement();
        final Document doc = parentElement.getOwnerDocument();
        if (segment.isText())
        {
            parentElement.appendChild(doc.createTextNode(segment.getContent()));
        }
        else if (segment.isLink())
        {
            String url;
            if (segment.getContent().charAt(0) == '#' && (segment.getUrl() == null || segment.getUrl().isEmpty()))
            {
                // hashtag - determine URL to link to
                url = streamSearchLinkBuilder.buildHashtagSearchLink(segment.getContent(), null);
            }
            else
            {
                // "normal" link - target is known.
                url = segment.getUrl();
            }

            // May be internal or external; open in new window unless internal.
            if (url.charAt(0) == '#')
            {
                parent.add(new InlineHyperlink(segment.getContent(), url.substring(1)));
            }
            else
            {
                AnchorElement anchor = doc.createAnchorElement();
                anchor.setHref(url);
                anchor.appendChild(doc.createTextNode(segment.getContent()));
                anchor.setTarget("_blank");
                parentElement.appendChild(anchor);
            }
        }
        else if (segment.isTag() && "br/".equals(segment.getContent()))
        {
            parentElement.appendChild(doc.createBRElement());
        }
    }

    /**
     * Renders a list of segments into the supplied container element (appending to existing content).
     *
     * @param list
     *            The segments.
     * @param parent
     *            The container element.
     * @param streamSearchLinkBuilder
     *            For building links for hashtags.
     */
    public void renderList(final ContentSegment list, final ComplexPanel parent,
            final StreamSearchLinkBuilder streamSearchLinkBuilder)
    {
        for (ContentSegment segment = list; segment != null; segment = segment.getNext())
        {
            renderSegment(segment, parent, streamSearchLinkBuilder);
        }
    }
}
