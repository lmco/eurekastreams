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

import org.eurekastreams.server.domain.stream.StreamView.Type;
import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;

/**
 * This class provides a mapper to the retrieve a core StreamView id from Cache.
 *
 */
public class GetCoreStreamViewIdCacheMapper extends BaseArgCachedDomainMapper<Type, Long>
{

    /**
     * Retrieve the StreamView Id for the type of core StreamView designated by the parameter.
     * @param inRequest - Type of the core StreamView to return.
     * @return id of the stream view requested.
     * {@inheritDoc}.
     */
    @Override
    public Long execute(final Type inRequest)
    {
        Long idValue = null;
        switch(inRequest)
        {
            case EVERYONE:
                idValue = (Long) getCache().get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE);
                break;
            case PARENTORG:
                idValue = (Long) getCache().get(CacheKeys.CORE_STREAMVIEW_ID_PARENTORG);
                break;
            case PEOPLEFOLLOW:
                idValue = (Long) getCache().get(CacheKeys.CORE_STREAMVIEW_ID_PEOPLEFOLLOW);
                break;
            case STARRED:
                idValue = (Long) getCache().get(CacheKeys.CORE_STREAMVIEW_ID_STARRED);
                break;
            default:
                throw new IllegalArgumentException("This mapper does not support the supplied StreamView Type.");
        }
        return idValue;
    }

}
