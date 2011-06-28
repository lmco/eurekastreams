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
package org.eurekastreams.server.action.execution.notification.filter;

import java.util.Collection;
import java.util.Map;

import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Filter which blocks specific notification types from being sent via a channel.
 */
public class BlockTypeBulkFilter implements RecipientFilter
{
    /** Types to block. */
    private final Collection<NotificationType> blockedTypes;

    /**
     * Constructor.
     *
     * @param inBlockedTypes
     *            List of types to block.
     */
    public BlockTypeBulkFilter(final Collection<NotificationType> inBlockedTypes)
    {
        blockedTypes = inBlockedTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldFilter(final NotificationType inType, final PersonModelView inRecipient,
            final Map<String, Object> inProperties, final String inNotifierType)
    {
        return blockedTypes.contains(inType);
    }
}
