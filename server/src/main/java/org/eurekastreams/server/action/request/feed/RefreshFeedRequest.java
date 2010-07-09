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
package org.eurekastreams.server.action.request.feed;

import java.io.Serializable;

/**
 * Refresh a feed request.
 *
 */
public class RefreshFeedRequest implements Serializable
{
	/**
     * The serial version id.
     */
    private static final long serialVersionUID = -3784201424343368123L;

    /**
     * The new activity.
     */
    private Long feedId;

    /**
     * Constructor.
     * 
     * @param inFeedId
     *            the activity.
     */
    public RefreshFeedRequest(final Long inFeedId)
    {
    	feedId = inFeedId;
    }
    
    /**
     * Gets the feed id.
     * @return the feed id.
     */
    public Long getFeedId()
    {
    	return feedId;
    }
}
