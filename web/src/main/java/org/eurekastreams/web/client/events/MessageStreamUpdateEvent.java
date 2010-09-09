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
package org.eurekastreams.web.client.events;

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * MessageStreamUpdatedEvent Event.
 */
public final class MessageStreamUpdateEvent
{

    /**
     * The message.
     */
    private PagedSet<ActivityDTO> messages;

    /**
     * The streamview id.
     */
    private Long streamId;

    /**
     * Latest activity.
     */
    private Long latestActivity;

    /**
     * The "no results" message to show.
     */
    private String noResultsMessage;

    /** If there are more stream items available to be requested (i.e. this is not the last page). */
    private boolean moreResults = true;

    /**
     * Constructor.
     * 
     * @param inMessage
     *            the message.
     */
    public MessageStreamUpdateEvent(final PagedSet<ActivityDTO> inMessage)
    {
        messages = inMessage;
    }

    /**
     * @return the message associated with the event.
     */
    public PagedSet<ActivityDTO> getMessages()
    {
        return messages;
    }

    /**
     * Gets Event.
     * 
     * @return Event.
     */
    public static MessageStreamUpdateEvent getEvent()
    {
        return new MessageStreamUpdateEvent(null);
    }

    /**
     * @return the stream id.
     */
    public Long getStreamId()
    {
        return streamId;
    }

    /**
     * @param inStreamId
     *            the stream id.
     */
    public void setStreamId(final Long inStreamId)
    {
        streamId = inStreamId;
    }

    /**
     * @return the noResultsMessage
     */
    public String getNoResultsMessage()
    {
        return noResultsMessage;
    }

    /**
     * @param inNoResultsMessage
     *            the noResultsMessage to set
     */
    public void setNoResultsMessage(final String inNoResultsMessage)
    {
        noResultsMessage = inNoResultsMessage;
    }

    /**
     * @param inMoreResults
     *            if there are more results
     */
    public void setMoreResults(final boolean inMoreResults)
    {
        moreResults = inMoreResults;
    }

    /**
     * @return if there are more results
     */
    public boolean isMoreResults()
    {
        return moreResults;
    }

    /**
     * Set the latest activity.
     * 
     * @param inLatestActivity
     *            the latest activity.
     */
    public void setLatestActivity(final Long inLatestActivity)
    {
        latestActivity = inLatestActivity;
    }

    /**
     * Get the latest activity.
     * 
     * @return the latest activity.
     */
    public Long getLatestActivity()
    {
        return latestActivity;
    }

}
