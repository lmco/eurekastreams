/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain.dto;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * DTO to contain different lists of StreamDTOs for the Streams Discover page.
 */
public class StreamDiscoverListsDTO implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -4114921089517370773L;

    /**
     * The number of streams with activities.
     */
    private Integer totalStreamCountWithActivities;

    /**
     * The top 10 most active streams by message count.
     */
    private ArrayList<StreamDTO> mostActiveStreams;

    /**
     * The top 10 most viewed streams.
     */
    private ArrayList<StreamDTO> mostViewedStreams;

    /**
     * The top 10 most followed streams.
     */
    private ArrayList<StreamDTO> mostFollowedStreams;

    /**
     * The top 10 most recent streams.
     */
    private ArrayList<StreamDTO> mostRecentStreams;

    /**
     * A list of the top 10 suggested streams for a specific person - this is populated after this is pulled from cache.
     */
    private ArrayList<StreamDTO> suggestedStreams;

    /**
     * A list of all of the currently featured streams.
     */
    private ArrayList<FeaturedStreamDTO> featuredStreams;

    /**
     * @return the totalStreamCountWithActivities
     */
    public Integer getTotalStreamCountWithActivities()
    {
        return totalStreamCountWithActivities;
    }

    /**
     * @param inTotalStreamCountWithActivities
     *            the totalStreamCountWithActivities to set
     */
    public void setTotalStreamCountWithActivities(final Integer inTotalStreamCountWithActivities)
    {
        totalStreamCountWithActivities = inTotalStreamCountWithActivities;
    }

    /**
     * @return the mostActiveStreams
     */
    public ArrayList<StreamDTO> getMostActiveStreams()
    {
        return mostActiveStreams;
    }

    /**
     * @param inMostActiveStreams
     *            the mostActiveStreams to set
     */
    public void setMostActiveStreams(final ArrayList<StreamDTO> inMostActiveStreams)
    {
        mostActiveStreams = inMostActiveStreams;
    }

    /**
     * @return the mostViewedStreams
     */
    public ArrayList<StreamDTO> getMostViewedStreams()
    {
        return mostViewedStreams;
    }

    /**
     * @param inMostViewedStreams
     *            the mostViewedStreams to set
     */
    public void setMostViewedStreams(final ArrayList<StreamDTO> inMostViewedStreams)
    {
        mostViewedStreams = inMostViewedStreams;
    }

    /**
     * @return the mostFollowedStreams
     */
    public ArrayList<StreamDTO> getMostFollowedStreams()
    {
        return mostFollowedStreams;
    }

    /**
     * @param inMostFollowedStreams
     *            the mostFollowedStreams to set
     */
    public void setMostFollowedStreams(final ArrayList<StreamDTO> inMostFollowedStreams)
    {
        mostFollowedStreams = inMostFollowedStreams;
    }

    /**
     * @return the mostRecentStreams
     */
    public ArrayList<StreamDTO> getMostRecentStreams()
    {
        return mostRecentStreams;
    }

    /**
     * @param inMostRecentStreams
     *            the mostRecentStreams to set
     */
    public void setMostRecentStreams(final ArrayList<StreamDTO> inMostRecentStreams)
    {
        mostRecentStreams = inMostRecentStreams;
    }

    /**
     * @return the suggestedStreams
     */
    public ArrayList<StreamDTO> getSuggestedStreams()
    {
        return suggestedStreams;
    }

    /**
     * @param inSuggestedStreams
     *            the suggestedStreams to set
     */
    public void setSuggestedStreams(final ArrayList<StreamDTO> inSuggestedStreams)
    {
        suggestedStreams = inSuggestedStreams;
    }

    /**
     * @return the featuredStreams
     */
    public ArrayList<FeaturedStreamDTO> getFeaturedStreams()
    {
        return featuredStreams;
    }

    /**
     * @param inFeaturedStreams
     *            the featuredStreams to set
     */
    public void setFeaturedStreams(final ArrayList<FeaturedStreamDTO> inFeaturedStreams)
    {
        featuredStreams = inFeaturedStreams;
    }

}
