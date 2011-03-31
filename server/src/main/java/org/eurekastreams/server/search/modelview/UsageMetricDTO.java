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
package org.eurekastreams.server.search.modelview;

import java.io.Serializable;

/**
 * Metrics DTO.
 * 
 */
public class UsageMetricDTO implements Serializable
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5498581567113276511L;

    /**
     * Stream view flag, default false.
     */
    boolean streamView = false;

    /**
     * Constructor.
     * 
     * @param inStreamView
     *            Stream view flag.
     */
    public UsageMetricDTO(final boolean inStreamView)
    {
        streamView = inStreamView;
    }

    /**
     * Constructor, uses default values.
     */
    public UsageMetricDTO()
    {
        // no-op.
    }

    /**
     * @return the streamView
     */
    public boolean isStreamView()
    {
        return streamView;
    }

    /**
     * @param inStreamView
     *            the streamView to set
     */
    public void setStreamView(final boolean inStreamView)
    {
        streamView = inStreamView;
    }

}
