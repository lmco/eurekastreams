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

import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Cache domain mapper to return the SystemSettings from cache.
 */
public class FindSystemSettingsCacheDomainMapperImpl extends CachedDomainMapper implements
        DomainMapper<MapperRequest<SystemSettings>, SystemSettings>
{
    /**
     * Constructor.
     *
     * @param inRequest
     *            the request object (not used)
     * @return the cached SystemSettings, or null if not found
     */
    public SystemSettings execute(final MapperRequest<SystemSettings> inRequest)
    {
        return (SystemSettings) getCache().get(CacheKeys.SYSTEM_SETTINGS);
    }

}
