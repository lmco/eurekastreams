/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

package org.eurekastreams.server.domain;

import java.io.Serializable;

//TODO must be brought out into the feed reader project.  

/**
 * DTO used to house feedReader Counting data to pass it from mapper to restlet.
 */
public class FeedReaderUrlCount implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 15464159784365474L;

    /**
     * Feed Title to display for feed.
     */
    private String feedTitle;
    
    /**
     *  Url for feed. 
     */
    private String url;

    /**
     * People in list with this feed.
     */
    private Long count;

    /**
     * Empty constructor for serialization.
     */
    public FeedReaderUrlCount()
    {
       //Empty constructor for serialization. 
    }


    /**
     * @return the feedTitle
     */
    public String getFeedTitle()
    {
        return feedTitle;
    }

    /**
     * @param inFeedTitle the feedTitle to set.
     */
    public void setFeedTitle(final String inFeedTitle)
    {
        feedTitle = inFeedTitle;
    }
    
    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param inUrl
     *            the url to set.
     */
    public void setUrl(final String inUrl)
    {
        url = inUrl;
    }

    /**
     * @return the count
     */
    public Long getCount()
    {
        return count;
    }

    /**
     * @param inCount
     *            the count to set.
     */
    public void setCount(final Long inCount)
    {
        count = inCount;
    }

}
