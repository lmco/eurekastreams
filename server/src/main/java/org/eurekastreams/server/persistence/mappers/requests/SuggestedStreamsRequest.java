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
package org.eurekastreams.server.persistence.mappers.requests;

/**
 * Request to get a list of suggested streams for a person.
 */
public class SuggestedStreamsRequest
{
    /**
     * The id of the person to get suggested streams for.
     */
    private long personId;

    /**
     * The number of stream suggestions.
     */
    private int streamCount;

    /**
     * Constructor.
     * 
     * @param inPersonId
     *            the id of the person to get suggested streams for.
     * @param inStreamCount
     *            the number of stream suggestions.
     */
    public SuggestedStreamsRequest(final long inPersonId, final int inStreamCount)
    {
        personId = inPersonId;
        streamCount = inStreamCount;
    }

    /**
     * @return the personId
     */
    public long getPersonId()
    {
        return personId;
    }

    /**
     * @param inPersonId
     *            the personId to set
     */
    public void setPersonId(final long inPersonId)
    {
        personId = inPersonId;
    }

    /**
     * @return the streamCount
     */
    public int getStreamCount()
    {
        return streamCount;
    }

    /**
     * @param inStreamCount
     *            the streamCount to set
     */
    public void setStreamCount(final int inStreamCount)
    {
        streamCount = inStreamCount;
    }

}
