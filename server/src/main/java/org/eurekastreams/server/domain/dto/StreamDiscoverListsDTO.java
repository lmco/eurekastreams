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
import java.util.List;

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
     * The top 10 most active streams by message count.
     */
    private SublistWithResultCount<StreamDTO> mostActiveStreams;

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
     * @return the mostActiveStreams
     */
    public SublistWithResultCount<StreamDTO> getMostActiveStreams()
    {
        return mostActiveStreams;
    }

    /**
     * @param inMostActiveStreams
     *            the mostActiveStreams to set
     */
    public void setMostActiveStreams(final SublistWithResultCount<StreamDTO> inMostActiveStreams)
    {
        mostActiveStreams = inMostActiveStreams;

        // convert the list to ArrayList for serialization
        mostActiveStreams.setResultsSublist(new ArrayList<StreamDTO>(mostActiveStreams.getResultsSublist()));
    }

    /**
     * @return the mostViewedStreams
     */
    public List<StreamDTO> getMostViewedStreams()
    {
        return mostViewedStreams;
    }

    /**
     * @param inMostViewedStreams
     *            the mostViewedStreams to set
     */
    public void setMostViewedStreams(final List<StreamDTO> inMostViewedStreams)
    {
        mostViewedStreams = new ArrayList<StreamDTO>(inMostViewedStreams);
    }

    /**
     * @return the mostFollowedStreams
     */
    public List<StreamDTO> getMostFollowedStreams()
    {
        return mostFollowedStreams;
    }

    /**
     * @param inMostFollowedStreams
     *            the mostFollowedStreams to set
     */
    public void setMostFollowedStreams(final List<StreamDTO> inMostFollowedStreams)
    {
        mostFollowedStreams = new ArrayList<StreamDTO>(inMostFollowedStreams);
    }

    /**
     * @return the mostRecentStreams
     */
    public List<StreamDTO> getMostRecentStreams()
    {
        return mostRecentStreams;
    }

    /**
     * @param inMostRecentStreams
     *            the mostRecentStreams to set
     */
    public void setMostRecentStreams(final List<StreamDTO> inMostRecentStreams)
    {
        mostRecentStreams = new ArrayList<StreamDTO>(inMostRecentStreams);
    }

    /**
     * @return the suggestedStreams
     */
    public List<StreamDTO> getSuggestedStreams()
    {
        return suggestedStreams;
    }

    /**
     * @param inSuggestedStreams
     *            the suggestedStreams to set
     */
    public void setSuggestedStreams(final List<StreamDTO> inSuggestedStreams)
    {
        suggestedStreams = new ArrayList<StreamDTO>(inSuggestedStreams);
    }

    /**
     * @return the featuredStreams
     */
    public List<FeaturedStreamDTO> getFeaturedStreams()
    {
        return featuredStreams;
    }

    /**
     * @param inFeaturedStreams
     *            the featuredStreams to set
     */
    public void setFeaturedStreams(final List<FeaturedStreamDTO> inFeaturedStreams)
    {
        featuredStreams = new ArrayList<FeaturedStreamDTO>(inFeaturedStreams);
    }

}
