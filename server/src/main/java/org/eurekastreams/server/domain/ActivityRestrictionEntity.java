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
package org.eurekastreams.server.domain;

/**
 * Brings together methods that are used for restricting items on a stream.
 */
public interface ActivityRestrictionEntity
{
    /**
     * @return if element is able to restrict stream Post.
     */
    boolean isStreamPostable();

    /**
     * @return if element is able to restrict comment on stream Post.
     */
    boolean isCommentable();

}
