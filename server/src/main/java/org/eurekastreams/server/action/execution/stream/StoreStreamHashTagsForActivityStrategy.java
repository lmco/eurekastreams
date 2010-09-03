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
package org.eurekastreams.server.action.execution.stream;

import org.eurekastreams.server.domain.stream.Activity;

/**
 * Parse an Activity's content, find any hashtags, then store the hashtags in each stream that the activity falls under,
 * including the person or group destination stream, as well as all orgs up the hierarchy, if the activity is public.
 */
public interface StoreStreamHashTagsForActivityStrategy
{
    /**
     * Parse and insert any necessary StreamHashTags for the input activity.
     *
     * @param inActivity
     *            the activity to parse for stream hashtags
     */
    void execute(final Activity inActivity);
}
