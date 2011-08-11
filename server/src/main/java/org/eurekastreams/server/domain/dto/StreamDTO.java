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
package org.eurekastreams.server.domain.dto;

import java.util.Date;

import org.eurekastreams.server.domain.Followable;

/**
 * Interface for StreamDTO implementations.
 * 
 */
public interface StreamDTO extends Followable, DisplayInfoSettable
{
    /**
     * @return stream entity id.
     */
    long getStreamId();

    /**
     * @return Title.
     */
    String getTitle();

    /**
     * @return Avatar id.
     */
    String getAvatarId();

    /**
     * @return the date added.
     */
    Date getDateAdded();

    /**
     * @return the stream scope id.
     */
    Long getStreamScopeId();

    /**
     * Whether the stream is public.
     * 
     * @return whether the stream is public
     */
    Boolean isPublic();
}
