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
package org.eurekastreams.web.client.events;

/**
 * Stream request.
 */
public class StreamRequestEvent
{
    /**
     * The JSON request.
     */
    private String json;

    /**
     * The stream name.
     */
    private String streamName;

    /**
     * The stream ID.
     */
    private Long streamId;

    /**
     * Force reload.
     */
    private Boolean forceReload = false;;

    /**
     * Constructor.
     * 
     * @param inStreamName
     *            the stream name.
     * @param inJson
     *            the json.
     */
    public StreamRequestEvent(final String inStreamName, final String inJson)
    {
        this(inStreamName, inJson, false);
    }

    /**
     * Constructor.
     * 
     * @param inStreamName
     *            the stream name.
     * @param inJson
     *            the json.
     * @param inForceReload
     *            if a reload should be forced.
     */
    public StreamRequestEvent(final String inStreamName, final String inJson, final Boolean inForceReload)
    {
        streamName = inStreamName;
        json = inJson;
        forceReload = inForceReload;
    }

    /**
     * Constructor.
     * 
     * @param inStreamName
     *            the stream name.
     * @param inStreamId
     *            the stream ID.
     * @param inJson
     *            the json.
     */
    public StreamRequestEvent(final String inStreamName, final Long inStreamId, final String inJson)
    {
        this(inStreamName, inStreamId, inJson, false);
    }

    /**
     * Constructor.
     * 
     * @param inStreamName
     *            the stream name.
     * @param inStreamId
     *            the stream ID.
     * @param inJson
     *            the json.
     * @param inForceReload
     *            if a reload should be forced.
     */
    public StreamRequestEvent(final String inStreamName, final Long inStreamId, final String inJson,
            final Boolean inForceReload)
    {
        streamName = inStreamName;
        streamId = inStreamId;
        json = inJson;
        forceReload = inForceReload;
    }

    /**
     * Get the JSON.
     * 
     * @return the json.
     */
    public String getJson()
    {
        return json;
    }

    /**
     * Get the stream name.
     * 
     * @return the stream name.
     */
    public String getStreamName()
    {
        return streamName;
    }

    /**
     * Get the stream ID.
     * 
     * @return the stream ID.
     */
    public Long getStreamId()
    {
        return streamId;
    }

    /**
     * Get if a reload should be forced.
     * 
     * @return if a reload should be forced.
     */
    public Boolean getForceReload()
    {
        return forceReload;
    }
}
