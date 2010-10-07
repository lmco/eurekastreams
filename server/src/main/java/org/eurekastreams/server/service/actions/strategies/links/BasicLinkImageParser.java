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
package org.eurekastreams.server.service.actions.strategies.links;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.LinkInformation;

/**
 * Parses images out of HTML.
 */
public class BasicLinkImageParser implements HtmlLinkInformationParserStrategy
{
    /**
     * URL Utilities.
     */
    private ConnectionFacade urlUtilities;

    /**
     * Minimum size for images.
     */
    public static final int MIN_IMG_SIZE = 60;

    /**
     * The max results to look for.
     */
    private int maxResults = 0;

    /**
     * Time out in milliseconds.
     */
    private long timeOut;

    /**
     * Constructor.
     * 
     * @param inUrlUtilities
     *            The URL utilities.
     * @param inMaxResults
     *            the max number of images to return.
     * @param inTimeOut
     *            the amount of milliseconds to allow image searching for.
     */
    public BasicLinkImageParser(final ConnectionFacade inUrlUtilities, final int inMaxResults, final long inTimeOut)
    {
        urlUtilities = inUrlUtilities;
        maxResults = inMaxResults;
        timeOut = inTimeOut;
    }

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(BasicLinkImageParser.class);

    /**
     * Parse the HTML.
     * 
     * @param htmlString
     *            the HTML as a string.
     * @param link
     *            the link.
     * @param inAccountId
     *            account id of the user making the request for the link image.
     */
    public void parseInformation(final String htmlString, final LinkInformation link, final String inAccountId)
    {
        Set<String> imageUrls = new HashSet<String>();

        /**
         * Determine if the link is an image.
         */
        Matcher isImagePattern = Pattern.compile(".*\\.(jpg|png|gif|jpeg)", Pattern.CASE_INSENSITIVE).matcher(
                link.getUrl());

        if (isImagePattern.find())
        {
            imageUrls.add(link.getUrl());
            link.setImageUrls(imageUrls);
            return;
        }

        /**
         * Find images.
         */
        Pattern imagePattern = Pattern.compile("<img[^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher imgMatcher = imagePattern.matcher(htmlString);

        long start = System.currentTimeMillis();

        int largestImgScore = 0;

        while (imgMatcher.find())
        {
            String img = imgMatcher.group();

            // Look for size tags to not download images that are clearly too small.
            Matcher widthHeightMatch = Pattern.compile("(width|height)\\s?=\\s?[\"\']?([0-5]?[0-9])[\"\'\\s]").matcher(
                    img);

            if (!widthHeightMatch.find())
            {

                Pattern imgSrcPattern = Pattern.compile("src\\s?=\\s?[\"\']?([^\"\'\\s]*)", Pattern.CASE_INSENSITIVE);
                Matcher srcMatcher = imgSrcPattern.matcher(img);

                if (srcMatcher.find())
                {
                    String imgUrl = srcMatcher.group(1);

                    try
                    {
                        String linkUrl = link.getUrl();

                        if (imgUrl.startsWith("/"))
                        {
                            log.trace("Image URL started with '/'");
                            imgUrl = urlUtilities.getProtocol(linkUrl) + "://" + urlUtilities.getHost(linkUrl) + imgUrl;
                        }
                        else if (!(imgUrl.startsWith("http://") || imgUrl.startsWith("https://")))
                        {
                            log.trace("No protocol found");
                            log.trace("Link URL: " + linkUrl);

                            if (linkUrl.indexOf("/", "https://".length() + 1) != -1)
                            {
                                imgUrl = linkUrl.substring(0, linkUrl.lastIndexOf("/") + 1) + imgUrl;
                            }
                            else
                            {
                                imgUrl = linkUrl + "/" + imgUrl;                                
                            }
                            
                            log.trace("New Image Url: " + imgUrl);
                        }

                        int height = urlUtilities.getImgHeight(imgUrl, inAccountId);
                        int width = urlUtilities.getImgWidth(imgUrl, inAccountId);

                        if (height > MIN_IMG_SIZE && width > MIN_IMG_SIZE)
                        {
                            if ((height * width) > largestImgScore)
                            {
                                largestImgScore = height * width;
                                link.setLargestImageUrl(imgUrl);
                            }

                            imageUrls.add(imgUrl);

                            if (imageUrls.size() == maxResults)
                            {
                                break;
                            }
                        }
                    }

                    catch (MalformedURLException e)
                    {
                        log.error("Error parsing URL: " + e);
                    }
                    catch (IOException e)
                    {
                        log.error("Error parsing URL: " + e);
                    }

                }
            }

            if (System.currentTimeMillis() - start > timeOut)
            {
                break;
            }
        }

        link.setImageUrls(imageUrls);

    }
}
