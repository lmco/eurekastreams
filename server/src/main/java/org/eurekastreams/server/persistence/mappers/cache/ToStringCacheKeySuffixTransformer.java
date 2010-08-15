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
package org.eurekastreams.server.persistence.mappers.cache;

/**
 * Cache key suffix transformer that calls toString on the object.
 */
public class ToStringCacheKeySuffixTransformer implements CacheKeySuffixTransformer<Object>
{
    /**
     * Return toString() on the input object.
     *
     * @param inSuffix
     *            the object to transform
     * @return a toString() on the input object
     */
    @Override
    public String transform(final Object inSuffix)
    {
        return inSuffix.toString();
    }
}
