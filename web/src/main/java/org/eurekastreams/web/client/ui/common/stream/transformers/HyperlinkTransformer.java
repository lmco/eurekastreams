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
package org.eurekastreams.web.client.ui.common.stream.transformers;

import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;

/**
 * Convert urls into hyperlinks.
 */
public class HyperlinkTransformer
{
    /**
     * JSNI facade.
     */
    private WidgetJSNIFacadeImpl jsni;

    /**
     * Constructor.
     *
     * @param inJsni
     *            the JSNI to escape the html content.
     */
    public HyperlinkTransformer(final WidgetJSNIFacadeImpl inJsni)
    {
        jsni = inJsni;
    }

    /**
     * Renders the content with urls converted to hyperlinks.
     *
     * @param inContent
     *            the content to hyperlink
     * @return the content with urls converted to hyperlinks.
     */
    public String transform(final String inContent)
    {
        // Strip out any existing HTML.
        String bodyText = jsni.escapeHtml(inContent);

        if (bodyText != null && !bodyText.equals(""))
        {
            int searchIndex = 0;
            int startIndex = 0;

            do
            {
                int[] urlIndex = new int[3];
                urlIndex[0] = bodyText.indexOf("http://", searchIndex);
                urlIndex[1] = bodyText.indexOf("https://", searchIndex);
                urlIndex[2] = bodyText.indexOf("www.", searchIndex);

                startIndex = -1;

                for (int i : urlIndex)
                {
                    if (i > -1)
                    {
                        if (startIndex == -1)
                        {
                            startIndex = i;
                        }
                        else if (i < startIndex)
                        {
                            startIndex = i;
                        }
                    }
                }
                if (startIndex >= 0)
                {
                    int endIndex = -1;
                    int[] endUrlIndex = new int[9 + 1];
                    endUrlIndex[0] = bodyText.indexOf(" ", startIndex);
                    endUrlIndex[1] = bodyText.indexOf("\n", startIndex);
                    endUrlIndex[2] = bodyText.indexOf(". ", startIndex);
                    endUrlIndex[3] = bodyText.indexOf("! ", startIndex);
                    endUrlIndex[4] = bodyText.indexOf("? ", startIndex);
                    endUrlIndex[5] = bodyText.indexOf("!\n", startIndex);
                    endUrlIndex[6] = bodyText.indexOf("?\n", startIndex);
                    endUrlIndex[7] = bodyText.indexOf(".\n", startIndex);
                    endUrlIndex[8] = bodyText.indexOf(", ", startIndex);
                    endUrlIndex[9] = bodyText.indexOf(",\n", startIndex);

                    for (int x = 0; x < endUrlIndex.length; x++)
                    {
                        if (endUrlIndex[x] > -1)
                        {
                            // if endindex has not been set or if there is a closer end index.
                            if (endIndex == -1 || endUrlIndex[x] < endIndex)
                            {
                                endIndex = endUrlIndex[x];
                            }
                        }
                    }

                    // this deals with end indexes being at the end of the string.
                    if (endIndex == -1)
                    {
                        endIndex = bodyText.length();

                        // if the last character before the end is a '.', don't include in the link.
                        char lastChar = bodyText.charAt(bodyText.length() - 1);
                        if (lastChar == '.' || lastChar == '?' || lastChar == '!')
                        {
                            endIndex--;
                        }
                    }

                    // just in case we're nested in parens, peel them off
                    if (openParenCount(bodyText, startIndex) > 0)
                    {
                        while (endIndex >= startIndex && bodyText.charAt(endIndex - 1) == ')')
                        {
                            endIndex--;
                        }
                    }

                    String url = bodyText.substring(startIndex, endIndex);
                    String linkableUrl = bodyText.substring(startIndex, endIndex);

                    if (linkableUrl.startsWith("www."))
                    {
                        linkableUrl = "http://" + linkableUrl;
                    }

                    String linkHtml = "<a target=\"_blank\" href=\"" + linkableUrl + "\">" + url + "</a>";

                    searchIndex = startIndex + linkHtml.length();

                    bodyText = bodyText.substring(0, startIndex) + linkHtml + bodyText.substring(endIndex);
                }
            }
            while (startIndex >= 0);
        }

        return bodyText;
    }

    /**
     * Return the count of open parens to the left of the input position.
     *
     * @param text
     *            the text to check
     * @param position
     *            the position to check
     * @return the count of open parens to the left of the input position
     */
    private int openParenCount(final String text, final int position)
    {
        int nestCount = 0;
        for (int i = 0; i < position; i++)
        {
            if (isInHrefBlock(text, i))
            {
                continue;
            }
            if (text.charAt(i) == ')')
            {
                if (nestCount > 0)
                {
                    nestCount--;
                }
            }
            else if (text.charAt(i) == '(')
            {
                nestCount++;
            }
        }
        return nestCount;
    }

    /**
     * Test whether the character at the input position is inside a hyperlink tag.
     *
     * @param content
     *            the content to check
     * @param pos
     *            the position to check in the content
     * @return whether the character at the input position is inside a hyperlink tag
     */
    private boolean isInHrefBlock(final String content, final int pos)
    {
        for (int p = pos - 1; p >= 0; p--)
        {
            String block = content.substring(p, pos).toLowerCase();
            if (block.contains("</a>"))
            {
                return false;
            }
            if (block.contains("<a "))
            {
                return true;
            }
        }
        return false;
    }
}
