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
 * A substring found in another string.
 */
public class Substring
{
    /**
     * The starting index of the substring.
     */
    private int startIndex;

    /**
     * The length of the substring.
     */
    private int length;

    /**
     * The content of the substring.
     */
    private String content;

    /**
     * The start index of the substring.
     *
     * @return start index of the substring
     */
    public int getStartIndex()
    {
        return startIndex;
    }

    /**
     * The length of the content.
     *
     * @return the length of the content
     */
    public int getLength()
    {
        return length;
    }

    /**
     * The content of the substring.
     *
     * @return the content
     */
    public String getContent()
    {
        return content;
    }

    /**
     * Constructor.
     *
     * @param inStartIndex
     *            the starting index of the substring
     * @param inLength
     *            the length of the substring
     * @param inContent
     *            the content of the substring
     */
    public Substring(final int inStartIndex, final int inLength, final String inContent)
    {
        startIndex = inStartIndex;
        length = inLength;
        content = inContent;
    }

    /**
     * Get the string representation of the hashtag.
     *
     * @return the content of the hashtag
     */
    @Override
    public String toString()
    {
        return content;
    }

}
