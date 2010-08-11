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
package org.eurekastreams.server.persistence.mappers.chained;

/**
 * Result combiner that returns one of the results that's not null.
 *
 * @param <T>
 *            the type of response to handle
 */
public class NonNullResultsCombiner<T> implements ResultsCombinerStrategy<T>
{
    /**
     * Return the first non-null value.
     *
     * @param inResponse1
     *            the first response to return if not null
     * @param inResponse2
     *            the second response to return if not null
     * @return inResponse1 if not null, else inResponse2
     */
    @Override
    public T combine(final T inResponse1, final T inResponse2)
    {
        return inResponse1 != null ? inResponse1 : inResponse2;
    }
}
