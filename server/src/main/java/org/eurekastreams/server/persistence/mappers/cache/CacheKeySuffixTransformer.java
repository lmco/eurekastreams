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
 * Transform a cache key suffix to what's expected in cache.
 * 
 * @deprecated Use Transformer interface directly.
 * 
 * @param <SuffixType>
 *            the type of suffix to transform to String
 */
@Deprecated
public interface CacheKeySuffixTransformer<SuffixType> extends Transformer<SuffixType, String>
{
    /**
     * Transform the input suffix for cache key lookup.
     * 
     * @param suffix
     *            the suffix to transform
     * @return a transformed string version of the key suffix
     */
    String transform(SuffixType suffix);
}
