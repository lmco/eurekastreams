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
package org.eurekastreams.web.client.ui.common.stream.renderers;

/**
 * Whether to show the recipient line on an activity.
 */
public enum ShowRecipient
{
    /**
     * Show for all activities, except where the author posted to their own stream. (Don't want to see
     * "John Doe to John Doe".) Author may be a group, such as for stream plugins.
     */
    YES,
    /**
     * Only show when the activity is in a resource stream. This will cause activities which are "shared to Eureka" from
     * the Comment Widget to have the "commented on ..." when shown in normal streams without having the "to ..." for
     * everything else.
     */
    RESOURCE_ONLY,
    /** Never show. */
    NO
}
