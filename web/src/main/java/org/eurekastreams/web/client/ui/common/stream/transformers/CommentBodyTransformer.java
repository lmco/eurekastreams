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
package org.eurekastreams.web.client.ui.common.stream.transformers;

import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;

/**
 * Transforms a comment body from text to HTML.
 */
public class CommentBodyTransformer
{
    /** JSNI Facade. */
    private final WidgetJSNIFacadeImpl jsniFacade;

    /** For transforming URLs to links. */
    private final HyperlinkTransformer hyperlinkTransformer;

    /** For transforming hashtags to links. */
    private final HashtagLinkTransformer hashtagLinkTransformer = new HashtagLinkTransformer(
            new StreamSearchLinkBuilder());

    /**
     * Constructor.
     * 
     * @param inJSNIFacade
     *            JSNI Facade
     */
    public CommentBodyTransformer(final WidgetJSNIFacadeImpl inJSNIFacade)
    {
        jsniFacade = inJSNIFacade;
        hyperlinkTransformer = new HyperlinkTransformer(jsniFacade);
    }


    /**
     * Takes the comment text and converts it to HTML for display.
     * 
     * @param inText
     *            The raw comment text.
     * @return Comment HTML.
     */
    public String transform(final String inText)
    {
        // Strip out any existing HTML.
        String commentBody = jsniFacade.escapeHtml(inText);
        commentBody = commentBody.replaceAll("(\r\n|\n|\r)", "<br />");

        // first transform links to hyperlinks
        commentBody = hyperlinkTransformer.transform(commentBody);

        // then transform hashtags to hyperlinks
        commentBody = hashtagLinkTransformer.transform(commentBody);

        return commentBody;
    }

    /**
     * Determines where in the string to truncate.
     * 
     * @param inString
     *            Pre-processed comment body.
     * @param inLength
     *            Desired truncation length.
     * @return Position in string.
     */
    public int determineTruncationPoint(final String inString, final int inLength)
    {
        // look for a start
        int tagStartPos = inString.lastIndexOf('<', inLength);
        if (tagStartPos < 0)
        {
            return inLength;
        }
        int tagEndPos = inString.indexOf('>', tagStartPos);

        // is this an end tag or a standalone tag?
        if (inString.charAt(tagStartPos + 1) != '/' && inString.charAt(tagEndPos - 1) != '/')
        {
            // No. So must be a start tag, so look for the end of the end tag
            // (Note: Since the HTML is well-formed and there will be no nested elements, the next > must be the end of
            // the closing tag.
            tagEndPos = inString.indexOf('>', tagEndPos + 1);
        }

        return tagEndPos >= inLength ? tagEndPos + 1 : inLength;
    }

}
