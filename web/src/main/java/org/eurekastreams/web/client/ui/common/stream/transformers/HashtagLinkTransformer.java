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
package org.eurekastreams.web.client.ui.common.stream.transformers;

/**
 * Transform valid hashtags into stream searches for the current stream.
 */
public class HashtagLinkTransformer
{
    /**
     * The helper to build hyperlinks to stream search.
     */
    private StreamSearchLinkBuilder streamSearchLinkBuilder;

    /**
     * Constructor.
     *
     * @param inStreamSearchLinkBuilder
     *            the helper to build hyperlinks to stream search
     */
    public HashtagLinkTransformer(final StreamSearchLinkBuilder inStreamSearchLinkBuilder)
    {
        streamSearchLinkBuilder = inStreamSearchLinkBuilder;
    }

    /**
     * Transform all valid hashtags that aren't already inside a hyperlink tag to hyperlink tags with links to search
     * the stream with the current stream view id.
     *
     * @param content
     *            the content to transform
     * @return a transformation of the input content with all valid hashtags that aren't already inside a hyperlink tag
     *         to hyperlink tags with links to search the stream with the input view id
     */
    public String transform(final String content)
    {
        return transform(content, null);
    }

    /**
     * Transform all valid hashtags that aren't already inside a hyperlink tag to hyperlink tags with links to search
     * the stream with the input view id.
     *
     * @param content
     *            the content to transform
     * @param streamViewId
     *            the stream view id to scope the hashtag search
     * @return a transformation of the input content with all valid hashtags that aren't already inside a hyperlink tag
     *         to hyperlink tags with links to search the stream with the input view id
     */
    public String transform(final String content, final Long streamViewId)
    {
        HashTagExtractor hashTagExtractor = new HashTagExtractor();

        if (content.indexOf('#') == -1)
        {
            // no hashtags
            return content;
        }

        Substring hashTag;
        StringBuffer sb = new StringBuffer();
        int pos = 0;

        while ((hashTag = hashTagExtractor.extract(content, pos)) != null)
        {
            // add everything up to this point
            sb.append(content.substring(pos, hashTag.getStartIndex()));

            // add the linked hashtag
            sb.append("<a href=\"" + streamSearchLinkBuilder.buildHashtagSearchLink(hashTag.getContent(), streamViewId)
                    + "\">" + hashTag.getContent() + "</a>");

            pos = hashTag.getStartIndex() + hashTag.getLength();
        }

        // append everything after the last hashTag
        sb.append(content.substring(pos));
        return sb.toString();
    }
}
