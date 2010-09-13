/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.stream.Stream;

/**
 * Gets fired when a stream view has been created.
 *
 */
public class CustomStreamCreatedEvent
{
    /**
     * Gets an instance of the event.
     *
     * @return the event.
     */
    public static CustomStreamCreatedEvent getEvent()
    {
        return new CustomStreamCreatedEvent(null);
    }

    /**
     * The new view.
     */
    private Stream stream;

    /**
     * Default constructor.
     *
     * @param inStream
     *            the new view.
     */
    public CustomStreamCreatedEvent(final Stream inStream)
    {
        stream = inStream;
    }

    /**
     * Returns the view.
     *
     * @return the view.
     */
    public Stream getStream()
    {
        return stream;
    }
}
