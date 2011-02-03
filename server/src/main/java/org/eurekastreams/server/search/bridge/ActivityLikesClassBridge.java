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
package org.eurekastreams.server.search.bridge;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.search.bridge.strategies.ComputeInterestingnessOfActivityStrategy;
import org.hibernate.search.bridge.StringBridge;

/**
 * Indexes the interestingness of an activity. A strategy is used to calculate this value.
 * Strategy only weights likes.
 */
public class ActivityLikesClassBridge implements StringBridge
{
    /**
     * The interestingness computing strategy.
     */
    private static ComputeInterestingnessOfActivityStrategy interstingnessStrategy;

    /**
     * Set the interestingness strategy.
     * 
     * @param inInterstingnessStrategy
     *            the strategy.
     */
    public static void setInterstingnessStrategy(
            final ComputeInterestingnessOfActivityStrategy inInterstingnessStrategy)
    {
        ActivityLikesClassBridge.interstingnessStrategy = inInterstingnessStrategy;
    }

    /**
     * Returning the interestingness (a Long) as a String.
     * 
     * @param msgObject
     *            the activity.
     * @return the interestingness.
     */
    public String objectToString(final Object msgObject)
    {
        Activity activity = (Activity) msgObject;

        return Long.toString(interstingnessStrategy.computeInterestingness(activity));
    }

}
