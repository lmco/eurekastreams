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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;

/**
 * Delete the system settings from cache - it'll lazy load when next requested.
 */
public class DeleteSystemSettingsCacheMapper extends BaseArgCachedDomainMapper<Object, Boolean>
{
    /**
     * Log.
     */
    private static Log log = LogFactory.make();

    /**
     * Delete the system settings from cache.
     *
     * @param inRequest
     *            ignored request
     * @return true
     */
    @Override
    public Boolean execute(final Object inRequest)
    {
        log.info("Clearing the SystemSettings from cache.");
        getCache().delete(CacheKeys.SYSTEM_SETTINGS);
        return true;
    }
}
