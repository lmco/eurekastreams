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
package org.eurekastreams.server.action.request;

import java.io.Serializable;

/**
 * A request that's pageable.
 *
 */
public interface PageableRequest extends Serializable
{
    /**
     * Get the start index for the request.
     *
     * @return the start index for the request.
     */
    Integer getStartIndex();

    /**
     * Set the start index for the request.
     *
     * @param inStartIndex
     *            the start index for the request.
     */
    void setStartIndex(final Integer inStartIndex);

    /**
     * Get the end index for the request.
     *
     * @return the end index for the request.
     */
    Integer getEndIndex();

    /**
     * Set the end index for the request.
     *
     * @param inEndIndex
     *            the end index for the request.
     */
    void setEndIndex(final Integer inEndIndex);

}
